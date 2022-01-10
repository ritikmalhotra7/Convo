package com.complete.convo.model

class Messages {
    var message :String? = null
    var senderId :String? = null
    var time : String? = null
    var date :String? = null
    constructor()
    constructor(message:String, time:String,senderId: String,date : String){
        this.message = message
        this.senderId = senderId
        this.time = time
        this.date = date
    }
}