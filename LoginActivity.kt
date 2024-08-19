import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ltbmodatatrackerv2.databinding.ActivityLoginBinding
import com.example.ltbmodatatrackerv2.databinding.ActivityLoginSignUpFrameBinding
import com.example.ltbmodatatrackerv2.databinding.ActivitySignupBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : ComponentActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    var loggedIn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("users")

        binding.loginButton.setOnClickListener {
            val loginUsername = binding.loginUsername.text.toString()
            val loginPassword = binding.loginPassword.text.toString()

            if (loginUsername.isNotEmpty() && loginPassword.isNotEmpty()) {
                loginUser(loginUsername, loginPassword)
            } else {
                Toast.makeText(this@LoginActivity, "User already exists.", Toast.LENGTH_SHORT).show()

            }
        }

        binding.loginRedirect.setOnClickListener{
            startActivity(Intent(this@LoginActivity, SignupActivity::class.java))
            finish()
        }

    }

    // when back is pressed, it is going to ask
    override fun onBackPressed() {
        super.onBackPressed()

        if (!ext.loggedInSession) {
            startActivity(Intent(this@LoginActivity, LoginActivity::class.java))
        }
    }
    private fun loginUser(username: String, password: String) {
        databaseReference.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()) {
                    for (userSnapshot in dataSnapshot.children) {
                        val userData = userSnapshot.getValue(UserData::class.java)
                        if (userData != null && userData.password == password) {
                            Toast.makeText(this@LoginActivity, "Login Successful", Toast.LENGTH_SHORT).show()
                            loggedIn = true
                            val intent = Intent (this@LoginActivity, MainActivity::class.java)
                            ext.loggedInSession = true
                            startActivity(intent) // pushes the event to the mainQR
                            finish()
                            return
                        } else {
                            Toast.makeText(this@LoginActivity, "User Already Exists", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                Toast.makeText(this@LoginActivity, "Login Failed", Toast.LENGTH_SHORT).show()

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@LoginActivity, "Database Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
