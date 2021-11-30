package com.complete.convo.model
class User {
    var name: String? = null
    var email: String? = null
    var uid: String? = null
    var phno : String? = null

    constructor()

    constructor(name: String?, email:String?, uid:String?){
        this.name = name
        this.email = email
        this.uid = uid
    }
    constructor(name: String?, phno : String?, uid:String?,phn: String?){
        this.name = name
        this.phno = phno
        this.uid = uid
    }
}