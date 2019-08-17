package info.eniuy.searchjapanamateurradiostationlicenceinformation

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*


class MainActivity : AppCompatActivity()//,LoaderManager.LoaderCallbacks<String>
{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //use selectTotalCount spinner [DC]
        val selectTotalCountMap: Map<String, String> =
            mapOf("1" to getString(R.string.cases_100), "2" to getString(R.string.cases_500), "3" to "10000件")
        val selectTotalCountList = arrayOf(selectTotalCountMap["1"], selectTotalCountMap["2"])//exclude DC=3
        //use selectRegionalBureauOfTelecommunications spinner [IT]
        val selectRegionalBureauOfTelecommunicationsMap: Map<String, String> =
            mapOf(
                "0" to "-",//0
                "J" to getString(R.string.hokkaido_bureau_of_telecommunications),
                "I" to getString(R.string.tohoku_bureau_of_telecommunications),
                "A" to getString(R.string.kanto_bureau_of_telecommunications),
                "B" to getString(R.string.shinetsu_bureau_of_telecommunications),
                "D" to getString(R.string.hokuriku_bureau_of_telecommunications),
                "C" to getString(R.string.tokai_bureau_of_telecommunications),
                "E" to getString(R.string.kinki_bureau_of_telecommunications),
                "F" to getString(R.string.chugoku_bureau_of_telecommunications),
                "G" to getString(R.string.shikoku_bureau_of_telecommunications),
                "H" to getString(R.string.kyushu_bureau_of_telecommunications),
                "O" to getString(R.string.okinawa_office_of_telecommunications)
            )
        val selectRegionalBureauOfTelecommunicationsList =
            arrayOf(
                selectRegionalBureauOfTelecommunicationsMap["0"],
                selectRegionalBureauOfTelecommunicationsMap["A"],
                selectRegionalBureauOfTelecommunicationsMap["B"],
                selectRegionalBureauOfTelecommunicationsMap["C"],
                selectRegionalBureauOfTelecommunicationsMap["D"],
                selectRegionalBureauOfTelecommunicationsMap["E"],
                selectRegionalBureauOfTelecommunicationsMap["F"],
                selectRegionalBureauOfTelecommunicationsMap["G"],
                selectRegionalBureauOfTelecommunicationsMap["H"],
                selectRegionalBureauOfTelecommunicationsMap["I"],
                selectRegionalBureauOfTelecommunicationsMap["J"],
                selectRegionalBureauOfTelecommunicationsMap["O"]
            )
        val selectTotalCount = findViewById<Spinner>(R.id.selectTotalCount)//spinner of selectTotalCount
        val selectRegionalBureauOfTelecommunications =
            findViewById<Spinner>(R.id.selectRegionalBureauOfTelecommunications)//spinner of selectRegionalBureauOfTelecommunications
        val inputCallSigns = findViewById<EditText>(R.id.inputCallSigns)//edit text of inputCallSigns [MA]
        val buttonClear = findViewById<Button>(R.id.buttonClear)//button of buttonClear
        val buttonSearch = findViewById<Button>(R.id.buttonSearch)//button of buttonSearch
        var selectTotalCountPosition: Int//selected selectTotalCount position
        var selectRegionalBureauOfTelecommunicationsPosition: Int//selected selectRegionalBureauOfTelecommunications position
        var inputCallSignsText: String//input inputCallSigns value
        var paramItVal: String//parameter IT value
        var paramDcVal: String//parameter DC value
        val regex = Regex("^[A-Z0-9]+$")//regex of check inputCallSigns
        val totalCountAdapter = ArrayAdapter(//create array adapter for totalCountSearch
            applicationContext,//set context
            android.R.layout.simple_spinner_item,
            selectTotalCountList//set array list
        )
        totalCountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        selectTotalCount.adapter = totalCountAdapter//set adapter for spinner

        /*
        //set listener
        selectTotalCount.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(//on Item selected
                parent: AdapterView<*>?,
                view: View?, position: Int, id: Long
            ) {
                val spinnerParent = parent as Spinner
                val totalCount = spinnerParent.selectedItem as String //selected value

            }//override fun onItemSelected
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }//on nothing selected
        }//selectTotalCount.onItemSelectedListener
        */

        val regionalBureauOfTelecommunicationsAdapter =
            ArrayAdapter(//create array adapter for regionalBureauOfTelecommunications
                applicationContext,//set context
                android.R.layout.simple_spinner_item,
                selectRegionalBureauOfTelecommunicationsList//set array list
            )
        regionalBureauOfTelecommunicationsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        selectRegionalBureauOfTelecommunications.adapter =
            regionalBureauOfTelecommunicationsAdapter//set adapter for spinner

        /*
        //set listener
        selectRegionalBureauOfTelecommunications.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?, position: Int, id: Long
            ) {//on Item selected
                val spinnerParent = parent as Spinner
                val regionalBureauOfTelecommunications = spinnerParent.selectedItem as String //selected value

            }//override fun onItemSelected
            override fun onNothingSelected(parent: AdapterView<*>?) {//on nothing selected

            }//override fun onNothingSelected
        }//selectRegionalBureauOfTelecommunications.onItemSelectedListener
        */

        buttonClear.setOnClickListener {
            //click buttonClear then
            //output log current selected data
            Log.d("before selectedTotalCountPosition", getSelectedSpinnerPosition(selectTotalCount).toString())
            Log.d(
                "before selectRegionalBureauOfTelecommunicationsPosition",
                getSelectedSpinnerPosition(selectRegionalBureauOfTelecommunications).toString()
            )
            Log.d("before inputCallSignsText", getInputTextValue(inputCallSigns))
            selectTotalCount.setSelection(0)//set position 0
            selectRegionalBureauOfTelecommunications.setSelection(0)//set position 0
            inputCallSigns.text.clear()//clear inputCallSigns value

        }

        buttonSearch.setOnClickListener {
            //click buttonSearch then
            selectTotalCountPosition = getSelectedSpinnerPosition(selectTotalCount)//selected selectTotalCount index
            selectRegionalBureauOfTelecommunicationsPosition =
                getSelectedSpinnerPosition(selectRegionalBureauOfTelecommunications)//selected selectRegionalBureauOfTelecommunications index
            inputCallSignsText = getInputTextValue(inputCallSigns)//entered inputCallSigns text
            //output log current selected value
            Log.d("selectTotalCountPosition", selectTotalCountPosition.toString())
            Log.d(
                "selectRegionalBureauOfTelecommunicationsPosition",
                selectRegionalBureauOfTelecommunicationsPosition.toString()
            )
            Log.d("selectRegionalBureauOfTelecommunicationsPosition", inputCallSignsText)

            paramDcVal = convertPositionIntToParamDcVal(selectTotalCountPosition)//set paramDcVal
            paramItVal =
                convertPositionIntToParamItVal(selectRegionalBureauOfTelecommunicationsPosition)//set paramItVal

            //validate inputCallSignsText
            if (inputCallSignsText.isNotEmpty() && !regex.matches(inputCallSignsText)) {
                Toast.makeText(//toast message
                    applicationContext,
                    getString(R.string.validCallSign),//please enter A-Z or 0-9
                    Toast.LENGTH_SHORT
                ).show()//toast valid error
                return@setOnClickListener//keep this activity
            }

            val csvRequestUrlString = createRequestUrl(//create csvRequestUrlString
                "1",
                paramDcVal,
                paramItVal,
                inputCallSignsText
            )

            val jsonRequestUrlString = createRequestUrl(//create jsonRequestUrlString
                "2",
                paramDcVal,
                paramItVal,
                inputCallSignsText
            )
            //intent to ViewRadioStationLicenceActivity
            val intentToViewRadioStationLicence = Intent(this@MainActivity, ViewRadioStationLicenceActivity::class.java)
            intentToViewRadioStationLicence.putExtra("csvRequestUrl", csvRequestUrlString)
            intentToViewRadioStationLicence.putExtra("jsonRequestUrl", jsonRequestUrlString)
            Log.d("jsonRequestUrl", jsonRequestUrlString)
            startActivity(intentToViewRadioStationLicence)

        }//buttonSearch.setOnClickListener

    }//override fun onCreate

    private fun getSelectedSpinnerPosition(spinner: Spinner): Int {
        return spinner.selectedItemPosition//selected selectTotalCount index
    }//getSelectedSpinnerPosition

    private fun getInputTextValue(editText: EditText): String {
        return editText.text.toString().toUpperCase()//get value and convert to upper case
    }//getInputTextValue

    private fun convertPositionIntToParamDcVal(position: Int): String {
        var paramDcVal = "0"//initialized paramDcVal
        when (position) {//set paramDcVal
            0 -> paramDcVal = "1"
            1 -> paramDcVal = "2"
        }
        return paramDcVal
    }//fun convertPositionIntToParamDcVal

    private fun convertPositionIntToParamItVal(position: Int): String {
        var paramItVal = ""//initialized paramItVal
        when (position) {//set paramItVal
            0 -> paramItVal = ""
            1 -> paramItVal = "A"
            2 -> paramItVal = "B"
            3 -> paramItVal = "C"
            4 -> paramItVal = "D"
            5 -> paramItVal = "E"
            6 -> paramItVal = "F"
            7 -> paramItVal = "G"
            8 -> paramItVal = "H"
            9 -> paramItVal = "I"
            10 -> paramItVal = "J"
            11 -> paramItVal = "O"

        }
        return paramItVal
    }//fun convertPositionIntToParamItVal

    private fun createRequestUrl(ofVal: String, dcVal: String, itVal: String, maVal: String): String {

        //set parameter
        val baseUrl = "https://www.tele.soumu.go.jp/musen/list?"
        val paramSt = "ST=1"//検索パターン
        val paramDa = "DA=1"//詳細情報付加
        val paramSc = "SC=1"//スタートカウント
        val paramDc = "DC=$dcVal"//取得件数
        val paramOf = "OF=$ofVal"//出力形式
        val paramOw = "OW=AT"//無線局の種別
        val paramMa = "MA=$maVal"//呼出符号
        val paramIt = "IT=$itVal"//所轄総合通信局

        val urlBuilder: StringBuilder = StringBuilder()//create request url
        urlBuilder.append(baseUrl)
        urlBuilder.append(paramSt)
        urlBuilder.append("&$paramDa")
        urlBuilder.append("&$paramSc")
        urlBuilder.append("&$paramDc")
        urlBuilder.append("&$paramOf")
        urlBuilder.append("&$paramOw")
        urlBuilder.append("&$paramMa")
        urlBuilder.append("&$paramIt")

        return urlBuilder.toString()//request url string
    }//fun createRequestUrl

}//class MainActivity : AppCompatActivity()
