
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

/*
*
*  The intention of this code is simple. retain all the qr codes scanned by the app. This will not show on ALL apps,
*  but the app itself on that particular phone. A simple list order is all needed in order to create a simple
*  list of scanned items.
* */


class QRListActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.list_pr) //creates the list and shows the current displayed qr code
        // show the qr code
        // first, get the intent:
        val txt:String = intent.getStringExtra("resultval").toString()
        // get date

        val time = Calendar.getInstance().time

        val formatter = SimpleDateFormat("HH:mm", Locale.JAPAN) // the locale is set to Japan
        val current = formatter.format(time)


        // equate the current formatted time as the value of the date scanned
        findViewById<TextView>(R.id.lastmessagetime).text = current.format(DateTimeFormatter.ofPattern("HH:mm"))
        findViewById<TextView>(R.id.prNumberId).text = txt



    }
}
