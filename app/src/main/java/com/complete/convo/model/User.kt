package com.complete.convo.model
class User {
    var name: String? = null
    var email: String? = null
    var uid: String? = null
    var phoneNumber : String? = null
    constructor()
    constructor(name: String?, email:String?, uid:String?){
        this.name = name
        this.email = email
        this.uid = uid
    }
    constructor(name: String?, phoneNumber : String?, uid:String?, phn: String?){
        this.name = name
        this.phoneNumber = phoneNumber
        this.uid = uid
    }
}