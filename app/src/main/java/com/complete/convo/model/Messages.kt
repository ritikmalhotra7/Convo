package com.complete.convo.model

class Messages {
    var message :String? = null
    var senderId :String? = null

    constructor(message:String, senderId: String){
        this.message = message
        this.senderId = senderId
    }
}