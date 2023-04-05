package com.example.walking.model

data class LikeDTO (
    //좋아요 카운트
    var likeCount: Int = 0,
    // 카운트 중복방지
    var likes: MutableMap<String, Boolean> = HashMap(),
    //게시물 아이디값
    var uc_seq: Int,
   //유저 아이디
    var email: String
    ) {

    data class Like(   var email: String? = null,
                       var uc_seq: Int? = null )
}




