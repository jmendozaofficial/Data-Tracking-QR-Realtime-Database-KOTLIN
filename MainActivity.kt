
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import java.lang.Integer.parseInt
import java.text.SimpleDateFormat
import java.util.Date


//import androidmads.library.qrgenerator.QRGContents
//import androidmads.library.qrgenerator.QRGEncoder



class MainActivity : AppCompatActivity() {
    private val qrResultList = ArrayList<String>()
    private var qrResultListArray = emptyArray<String>()
    // pr number department and what is the purpose
//    var ltb_department = ""
//    private var qrImage : Bitmap? = null
//    val EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 1
    private var textResult = mutableStateOf("")
//    private var selectedImageUri : Uri? = null
//    lateinit var bitmap: Bitmap
    private var progression = 0
//    private var storageReference = Firebase.storage.reference
//    lateinit var qrEncoder: QRGEncoder


    private var currTimeAction = ""
//    val dateFormat: DateFormat = SimpleDateFormat("hh:mm a")



//    private lateinit var binding : MainActivityLayoutBinding
//    private lateinit var databaseRef : DatabaseReference

//    lateinit var userInput : EditText
//    lateinit var passwordInput : EditText
//    lateinit var loginBtn : Button




    val database = Firebase.database
        val progresstab = FirebaseDatabase.getInstance().getReference("prProgressTableRow")


        @SuppressLint("SimpleDateFormat")
        private val barcodeLauncher = registerForActivityResult(ScanContract()) {
        result ->
        if (result.contents == null) {
            Toast.makeText(this@MainActivity, "Cancelled", Toast.LENGTH_SHORT).show()
        }

        else {
            textResult.value = result.contents // content of the qr code
            findViewById<TextView>(R.id.scannedText).text = textResult.value
            qrResultList.add(textResult.value)
            qrResultListArray += textResult.value
        }
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date()) // the current date variable should be with a simple date format


        currTimeAction = currentDate // the current time action should be equal to the canned current date

    }

    private fun showCamera() {
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setPrompt("Scan a QR Code")
        options.setCameraId(0)
        options.setBeepEnabled(false)
        options.setOrientationLocked(false)

        barcodeLauncher.launch(options)


    }
    // fetch data calls the firebase and checks the progress of the checking.
    /* this way, it prevents the system from dragging the checking on another file and instead
    * proceeds to the same file as long as the first process is not finished.
    * it should be 0 - 1 - 2 - 3 - 4
    * so, in order to prevent making the process stay from 0, 1, 1, 1, 1 whenever the app
    * is reset, it has to fetch the data from the database so that it will check the
    * current progress. If 1, then go to 2. if 2, then go to 3.
    * */
    private fun fetchData () {
        val db = Firebase.database.reference
        val progressRef = db.child("prProgressTableRow").child("progress")
        progressRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val progressSnapshot = task.result
                progression = parseInt(progressSnapshot.getValue().toString())

            }
        }
    }


    // request the launcher to show the camera if the access is granted (from the app settings)
    private val requestPermissionLaucher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        isGranted ->
        if (isGranted) {
            showCamera()
        }
    }


    // Send to Email Function
    private fun sendEmailIntent () {
        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "abc@gmail.com", null))
        startActivity(Intent.createChooser(emailIntent, "send email"))

    }

    // this will go to the qr generation.
    private fun gotoGenerateCodeQR () {
        val intent = Intent (this@MainActivity, QRGenerator::class.java)
        startActivity(intent) // pushes the event to the qr list
    }

    /**
     * this will make the item stay on the firebase as a message.
     * however, this will not be shown on the data table because this is just going to save the item
     * in the database. This will only make it have the data accessible by the administrator for further checking.
     * Just in case you want to make the application send the item in the database as a viewable item, you can
     * haul the message from the web app and sent into the data table with the key "message".
      */

    private fun sendToFirebaseAction() {
//        progression = parseInt(progresstab.child("progress").toString())

//        fetchdataOfProgression()
        fetchData ()

        // change the time given
        when (progression) {
            // Mayor Office 1
            // creates an individual array list of mayor's office
            /*
            *  this happens when the qr code is scanned first, meaning the second time that
            * the user scans, it will go to treasury office, treasury to budget,
            * budget to mayor's office. refer to mayors2.
            * */
            0-> {

                val c = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                val cd = c.format(Date())


                currTimeAction = cd // the current time action is equal to the formatted simple date.
                val mayorsofficemessage1= database.getReference("mayors1")
                val mayorsoffice1time = currTimeAction

                // create timestamp
                // send to the database
                mayorsofficemessage1.child("time").push().setValue(mayorsoffice1time)
                progresstab.child("progress").setValue(1) // set the value from the dabtabase. new value
                progression = 1
                Toast.makeText(this@MainActivity, "Scan Progress: $progression", Toast.LENGTH_SHORT).show()

                // disable the button of the firebase sending, not unless it goes back to 0.
//                findViewById<Button>(R.id.firebaseSend).isEnabled = false


            }
            // Treasury Office
            1-> {
                val t = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                val td = t.format(Date())


                currTimeAction = td
                // creates an individual array list of treasury office
                val treasuryofficemessage= database.getReference("treasuryoffice")
                val trofficetime = currTimeAction

                // create timestamp
                treasuryofficemessage.child("time").push().setValue(trofficetime)
                progresstab.child("progress").setValue(2)

                progression = 2
                Toast.makeText(this@MainActivity, "Scan Progress: $progression", Toast.LENGTH_SHORT).show()
            }

            // Budget Office
            2-> {
                val f = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                val fd = f.format(Date())


                currTimeAction = fd
                // creates an individual array list of budget office
                val budgetofficemessage= database.getReference("budgetoffice")
                val budgetofficetime = currTimeAction

                budgetofficemessage.child("time").push().setValue(budgetofficetime)
                progresstab.child("progress").setValue(3)
                progression = 3
                Toast.makeText(this@MainActivity, "Scan Progress: $progression", Toast.LENGTH_SHORT).show()
            }

            // Mayor's Office 2
            3-> {
                val md = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                val cdf = md.format(Date())


                currTimeAction = cdf
                // creates an individual array list of mayor's office
                val mayorsoffice2msg= database.getReference("mayors2")
                val mayorfsoffice2time = currTimeAction
                // create timestamp
                mayorsoffice2msg.child("time").push().setValue(mayorfsoffice2time)
                progresstab.child("progress").setValue(4)
                progression = 4
                Toast.makeText(this@MainActivity, "Scan Progress: $progression", Toast.LENGTH_SHORT).show()
            }
        }

        if (progression >= 4) {

            progression = 0
            progresstab.child("progress").setValue(0) // return to 0
//            findViewById<Button>(R.id.firebaseSend).isEnabled = true

        }

        // once the sending to the database is done, the notification text appears.
        Toast.makeText(this@MainActivity, "Sending Success", Toast.LENGTH_SHORT).show()
    }




    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        // when there is a logged in session, the window shall turn to the main activity. If not, it will return to the
//        // login section or signup section.
//        if (!ext.loggedInSession) { // if there's no logged in session.
//            val intent = Intent (this@MainActivity, LoginActivity::class.java) // create a new intent
//            startActivity(intent) // make the activity start by setting which intent.
//        }
//        val intent = Intent (this@MainActivity, LoginActivity::class.java)
//        startActivity(intent)
        // make the progress read first here, so whenever you open the app, it checks your progress:


            fetchData()


        // check the intent of the login:


        setContent {
                // setContentView (R.layout.activity_user_authentication) }



                // make the main activity frame use the main activity layout.
                // it will use all items from that layout available for adding functions, change value, etc.
                setContentView(R.layout.main_activity_layout) // once the content of the layout is set, we close the two items first.




                // this shows the session's current QR codes scanned and displayed as text (decoded).
                findViewById<Button>(R.id.currentqrlist)
                    .setOnClickListener{
                        val intent = Intent (this@MainActivity, QRSessionFullList::class.java)
                        intent.putExtra("qrResultListArray", qrResultListArray) // push the whole qr result list to be hauled by the next activity.
                        startActivity(intent) // pushes the event to the qr list

                    }

                // firebase button
                // the firebase button will call the sendToFirebaseAction function.
                findViewById<Button>(R.id.firebaseSend)
                    .setOnClickListener {
                        sendToFirebaseAction()
                    }

                // immediateqr button
                // opens the checkCameraPermission function
                findViewById<Button>(R.id.immediateqr)
                    .setOnClickListener {
                        checkCameraPermission(this@MainActivity)
                    }


                // Sends the email necessary through the app.
                findViewById<Button>(R.id.emailIntent)
                    .setOnClickListener {
                        sendEmailIntent()
                    }


                findViewById<Button>(R.id.qrgenbuttonconfirm).setOnClickListener {
                    gotoGenerateCodeQR()
                }

        }

    }


    // checks the camera permission through the ADV.
    // if the ADV detects the camera and accepts the permission, the user is then directed to
    // the scanning page.
    private fun checkCameraPermission(context: Context) {
        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED) {
            showCamera()
        }

        else if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
            // if the permission is not yet granted, this message will appear to ask the user and require him / her of the camera permission.
            Toast.makeText(this@MainActivity, "Camera Required", Toast.LENGTH_SHORT).show()

        }
        else {
            // launch permission
            requestPermissionLaucher.launch(android.Manifest.permission.CAMERA)
        }
    }
}
