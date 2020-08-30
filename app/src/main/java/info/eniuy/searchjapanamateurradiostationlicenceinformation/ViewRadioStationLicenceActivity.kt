package info.eniuy.searchjapanamateurradiostationlicenceinformation


import android.annotation.SuppressLint
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.browser.customtabs.CustomTabsIntent
import android.util.Log
import android.widget.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class ViewRadioStationLicenceActivity : AppCompatActivity() {
    private val musenInformation: String = "musenInformation"
    private val lastUpdateDate: String = "lastUpdateDate"
    private val totalCount: String = "totalCount"
    private val musen: String = "musen"
    private val detailInfo: String = "detailInfo"
    private val listInfo: String = "listInfo"
    private val identificationSignals: String = "identificationSignals"
    private val permittedOperatingHours: String = "permittedOperatingHours"
    private val licenseDate: String = "licenseDate"
    private val validTerms: String = "validTerms"
    private val movementArea: String = "movementArea"
    private val radioEquipmentLocation: String = "radioEuipmentLocation"
    private val radioSpec1: String = "radioSpec1"
    private val displayNumber: String = "no"
    //SimpleAdapter args #4
    private val simpleAdapterFrom = arrayOf(
        displayNumber,
        identificationSignals,
        permittedOperatingHours,
        licenseDate,
        validTerms,
        movementArea,
        radioEquipmentLocation,
        radioSpec1
    )
    //SimpleAdapter args #5
    private val simpleAdapterTo = intArrayOf(
        R.id.displayNumber,
        R.id.identificationSignalsValue,
        R.id.permittedOperatingHoursValue,
        R.id.licenseDateValue,
        R.id.validTermsValue,
        R.id.movementAreaValue,
        R.id.radioEquipmentLocationValue,
        R.id.radioSpec1Value
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_radio_station_licence)
        Log.d("ViewRadioStationLicenceActivity", "start ViewRadioStationLicenceActivity")
        val intent = intent //intent from MainActivity
        val csvRequestUrl = intent.getStringExtra("csvRequestUrl").toString()//csvRequestUrl from MainActivity
        val jsonRequestUrl = intent.getStringExtra("jsonRequestUrl").toString()//jsonRequestUrl from MainActivity
        val outputAsACsvButton = findViewById<Button>(R.id.output_as_a_csv)//outputAsACsvButton

        startRadioLicenceReceiver(jsonRequestUrl)//set radio licence list

        outputAsACsvButton.setOnClickListener {
            //outputAsACsvButton clicked then
            switchCustomTabIntent(csvRequestUrl)//open chrome custom tabs and download csv data
        }
    }//fun onCreate

    private fun startRadioLicenceReceiver(jsonRequestUrl: String) {
        val radioLicenceReceiver = RadioLicenceReceiver()//call RadioLicenceReceiver
        radioLicenceReceiver.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, jsonRequestUrl)//set parameter args[1] to ...
    }//fun startRadioLicenceReceiver

    private fun getMusenInformation(rootJson: JSONObject, informationType: String): String {
        val musenInformationObject = rootJson.getJSONObject(musenInformation)
        val informationTypeValue = musenInformationObject.getString(informationType)
        var returnVal: String = informationType
        when (informationType) {
            lastUpdateDate -> {
                returnVal = "${getString(R.string.latest_update_date)} $informationTypeValue"
            }
            totalCount -> {
                returnVal = "${getString(R.string.total_count)} $informationTypeValue"
            }
        }

        return returnVal
    }//fun getMusenInformation

    private fun createLicenceList(rootJson: JSONObject): MutableList<MutableMap<String, Any>> {
        val musenArray = rootJson.getJSONArray(musen)//array of detail info and list info
        val licenceList: MutableList<MutableMap<String, Any>> = mutableListOf()//licence list array
        var listValue: MutableMap<String, Any>//value of licence list

        for (i in 0 until musenArray.length()) {//set value of listValue
            listValue = mutableMapOf(
                displayNumber to "[${musenArray.getJSONObject(i).getJSONObject(listInfo).getString(displayNumber)}] ",
                identificationSignals to ": ${musenArray.getJSONObject(i).getJSONObject(detailInfo).getString(
                    identificationSignals
                )}",
                permittedOperatingHours to ": ${musenArray.getJSONObject(i).getJSONObject(detailInfo).getString(
                    permittedOperatingHours
                )}",
                licenseDate to ": ${musenArray.getJSONObject(i).getJSONObject(detailInfo).getString(licenseDate)}",
                validTerms to ": ${musenArray.getJSONObject(i).getJSONObject(detailInfo).getString(validTerms)}",
                movementArea to ": ${musenArray.getJSONObject(i).getJSONObject(detailInfo).getString(movementArea)}",
                radioEquipmentLocation to ": ${musenArray.getJSONObject(i).getJSONObject(detailInfo).getString(
                    radioEquipmentLocation
                )}",
                radioSpec1 to ": ${musenArray.getJSONObject(i).getJSONObject(detailInfo).getString(radioSpec1)}"
            )
            licenceList.add(listValue)//set listValue to licenceList
        }

        return licenceList
    }//fun createLicenceList

    private fun switchCustomTabIntent(url: String) {
        //add dependencies [implementation 'com.android.support:customtabs:28.0.0'] in build.gradle(Module: app)
        val customTabsIntentBuilder = CustomTabsIntent.Builder()
        val customTabsIntent = customTabsIntentBuilder.build()
        customTabsIntent.intent.setPackage("com.android.chrome")
        customTabsIntent.launchUrl(this@ViewRadioStationLicenceActivity, Uri.parse(url))
    }//fun switchCustomTabIntent


    @SuppressLint("StaticFieldLeak")
    private inner class RadioLicenceReceiver : AsyncTask<String, String, String>() {
        val searchResult: TextView = findViewById(R.id.search_result)//searchResult label
        override fun onPreExecute() {//before async job of ui thread
            super.onPreExecute()
            Log.d("RadioLicenceReceiver", "start onPreExecute")
            searchResult.setText(R.string.loading)//set value
        }

        override fun doInBackground(vararg params: String?): String {//async job
            Log.d("RadioLicenceReceiver", "start doInBackground")
            Log.d("URL", params[0].toString())

            val url = URL(params[0])//create url object, params[0] is jsonRequestUrl
            val httpsURLConnection =
                url.openConnection() as HttpsURLConnection//get HttpsURLConnection object from url object
            httpsURLConnection.requestMethod = "GET"//set method of http connect
            httpsURLConnection.connect()//connect
            val responseCode = httpsURLConnection.responseCode//response code
            val stream = httpsURLConnection.inputStream//get response data from HttpsURLConnection object
            val result = is2String(stream)//convert inputStream object by response data to string
            httpsURLConnection.disconnect()//disconnect, release HttpsURLConnection object
            stream.close()//release inputStream object

            publishProgress(responseCode.toString())//call onProgressUpdate

            return result
        }//fun doInBackground

        override fun onProgressUpdate(vararg values: String?) {//call from publishProgress
            super.onProgressUpdate(*values)
            Log.d("responseCode", values[0].toString())//http response code
        }//fun onProgressUpdate

        override fun onPostExecute(result: String?) {//finished doInBackground then call this on ui thread
            super.onPostExecute(result)
            Log.d("result", result.toString())

            val latestUpdateText = findViewById<TextView>(R.id.latest_update_date)
            val totalCountText = findViewById<TextView>(R.id.totalCount)
            val searchResultList = findViewById<ListView>(R.id.search_result_list)


            val rootJSON = JSONObject(result)//result string to JSON object [root object]

            val licenceList = createLicenceList(rootJSON)//item of list view, SimpleAdapter args #2
            val simpleAdapter = SimpleAdapter(//create SimpleAdapter
                applicationContext,
                licenceList,
                R.layout.row_licence_list,
                simpleAdapterFrom,
                simpleAdapterTo
            )
            searchResultList.adapter = simpleAdapter//set adapter to list view

            latestUpdateText.text = getMusenInformation(rootJSON, lastUpdateDate)//set latestUpdateText
            totalCountText.text = getMusenInformation(rootJSON, totalCount)//set totalCountText
            searchResult.setText(R.string.search_result)//change values

        }//fun onPostExecute

        private fun is2String(stream: InputStream): String {//convert inputStream object to string
            val sb = StringBuilder()
            val reader = BufferedReader(InputStreamReader(stream, "UTF-8"))
            var line = reader.readLine()
            while (line != null) {
                sb.append(line)
                line = reader.readLine()
            }
            reader.close()
            return sb.toString()
        }//fun is2String


    }//private inner class RadioLicenceReceiver: AsyncTask<String, String, String>()

}//class ViewRadioStationLicenceActivity : AppCompatActivity()
