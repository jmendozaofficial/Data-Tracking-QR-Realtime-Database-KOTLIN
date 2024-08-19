object ext {
    var loggedInSession : Boolean = false
    var scanFinish : Boolean = false
    var administrator : Boolean = false
    var versionApp : Double = 1.0

    var date = ""
    var office = ""
    var purpose = ""

}

data class UserData(
    val id: String? = null,
    val username: String? = null,
    val password: String? = null
)
