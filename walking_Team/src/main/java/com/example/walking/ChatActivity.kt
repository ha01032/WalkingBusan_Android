package com.example.walking

import android.annotation.SuppressLint
import android.app.ProgressDialog.show
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.walking.adapter.MemberInputAdapter
import com.example.walking.databinding.ActivityChatBinding
import com.example.walking.model.Comment
import com.example.walking.model.Meeting
import com.example.walking.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : AppCompatActivity(), PopupMenu.OnMenuItemClickListener {

    val TAG : String = "ChatActivity"

    lateinit var binding : ActivityChatBinding
    //Global field
    private val fireDatabase = FirebaseDatabase.getInstance().reference
    private var chatRoomId :String? = null
    private var memberList: List<String>? = null
    private var recyclerView : RecyclerView? = null

    private var tripId: String? = null
    private lateinit var comments:ArrayList<Comment>
    lateinit var chatAdapt:ChatListAdapter
    var username: String? = null
    var nickname: String? = null
    var admin: String? = null
    var meeting: Meeting? = null

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        comments = ArrayList<Comment>()
        chatAdapt = ChatListAdapter(comments)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)





        binding.btnmenu.setOnClickListener{
            if(!binding.drawer.isDrawerOpen(Gravity.RIGHT)) {
                binding.drawer.openDrawer(Gravity.RIGHT)
            } else {
                binding.drawer.closeDrawer(Gravity.RIGHT)
            }
        }




        val intent = intent
        var title = intent.getStringExtra("title")
        nickname = intent.getStringExtra("nickname")
        username = intent.getStringExtra("username")
        Log.d("chc","$title===================")
        val networkService = (applicationContext as MyApplication).networkService
        val oneMeetingCall = networkService.doGetOneMeeting(title)
        oneMeetingCall.enqueue(object: Callback<Meeting> {
            override fun onResponse(call: Call<Meeting>, response: Response<Meeting>) {
                meeting = response.body()
                admin = meeting?.email

                Log.d("ttttt", "${username} == ${admin}")
                if(admin == username){
                    binding.optionBtn.setOnClickListener { showPopup(binding.optionBtn) }}
                else{
                    binding.optionBtn.setOnClickListener{ showPopup2(binding.optionBtn)}
                }




                val layoutManager = LinearLayoutManager(this@ChatActivity)

                binding.startDate.text = meeting?.start_date
                binding.endDate.text = " ~ "+meeting?.end_date
                binding.meetingTitle.text = meeting?.meeting_title
                binding.meetingContent.text = meeting?.meeting_content
                binding.meetingPlace.text = meeting?.meeting_place_name
                binding.meetingid.text = meeting?.meeting_id.toString()
                Glide.with(this@ChatActivity)
                    .asBitmap()
                    .load(meeting?.meeting_place_imgurl)
                    .into(object : CustomTarget<Bitmap>(500, 200) {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            binding.placeImage.setImageBitmap(resource)
                            Log.d("chc", "width : ${resource.width}, height: ${resource.height}")
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {

                        }
                    })


//                val memberList:List<String>? = trip?.member?.split(",")
                Log.d("chcchc", "${meeting?.meeting_id}")
                val meetingListCall = networkService.doGetChatMemberList(meeting?.meeting_id)
                meetingListCall.enqueue(object: Callback<List<User>>{
                    override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                        val chatmemberList = response.body()
                        Log.d("meetinglistchc", "$chatmemberList")


                        var layoutManager = LinearLayoutManager(this@ChatActivity)
                        layoutManager.orientation = LinearLayoutManager.VERTICAL

                        binding.memberRecyclerView.layoutManager = layoutManager



                        binding.memberRecyclerView.adapter = MemberInputAdapter(chatmemberList,nickname, admin, this@ChatActivity)
                        Log.d("test","chat------------------------$nickname")

                        binding.countmember.text = "${chatmemberList?.size}명 참여중"


                        binding.drawerTitle.text = "${meeting?.meeting_title}      ${chatmemberList?.size}"

                    }
                    override fun onFailure(call: Call<List<User>>, t: Throwable) {
                        Log.d(TAG, "실패")
                    }
                })



                binding.mapbtn.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.co.kr/maps/search/${meeting?.meeting_place_spot}"))

                    startActivity(intent)
                }




            }
            override fun onFailure(call: Call<Meeting>, t: Throwable) {
                call.cancel()
            }
        })








        //채팅

        recyclerView = findViewById(R.id.chatListRecyclerView)

        //메시지 보낸 시간
        val time = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("MM월dd일 hh:mm")//한국 날짜 시간 변경
        val curTime = dateFormat.format(Date(time)).toString()

//        //받아온 데이터 : 멤버리스트, 여행 id, 여행 제목
//        memberList = intent.getStringExtra("member")?.split(",") //배열형으로 출력.[a1, a2, a3]

        val chatTitle = intent.getStringExtra("title")




        //방에 입장하면, 파이어베이스에 저장되도록! - 채팅방 번호, 멤버
        tripId = intent.getStringExtra("title")
        Log.d("test", "..................$tripId")

        if(chatRoomId == null){
            //Log.d("chc",memberList.toString())
            fireDatabase.child("chatrooms").child(tripId.toString()).child("chatmembers")
                .addListenerForSingleValueEvent(object:ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
//                        for(member in memberList!!){
//                            Log.d("test",">>>>> ${snapshot.children}")
//                            for(data in snapshot.children){
//                                Log.d("test",">>>>>${data.value} $member")
//                                if(data.value==member){
//                                    return
//                                }
//                                Log.d("test",data.toString())
//                            }
//                            fireDatabase.child("chatrooms").child(tripId.toString())
//                                .child("chatmembers")
//                                .push().setValue(member)
//                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                    }
                })
        }
        getMessageList() //List 호출

        //메세지 전송 버튼 클릭 시
        binding.chatSendBtn.setOnClickListener {
            val member = nickname //멤버 지정 - 추후 로그인한 사용자(자신)
            var editMsgBox = findViewById<TextView>(R.id.chatMsgEdit).text
            val comment = Comment(member, editMsgBox.toString(), curTime)
            Log.d("test", "chat...cmt...$comment")
            Log.d("chat", ".....$member")
            Log.d("chat", ".....$editMsgBox")
            Log.d("chat", ".....$curTime")
            fireDatabase.child("chatrooms").child(tripId.toString()).child("comments").push()
                .setValue(comment)
            binding.chatMsgEdit.setText("")
            fireDatabase.child("chatrooms").child(tripId.toString()).child("comments")
                .addListenerForSingleValueEvent(
                    object : ValueEventListener {

                        override fun onDataChange(snapshot: DataSnapshot) {

                            getMessageList()
                            chatAdapt.notifyDataSetChanged()


                        }

                        override fun onCancelled(error: DatabaseError) {
                        }
                    })



        }
        //editText 입력된 문자열 초기화 ?

//comment 저장하는 코드
//fireDatabase.child("chatrooms").child(tripId.toString()).child("comments").push().setValue(comment)

    }












    //recyclerViewAdapter
    inner class ChatListAdapter(val comments: List<Comment>): RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder>() {
        //        private val comments = ArrayList<Comment>()
        //채팅 메시지 레이아웃 붙이기
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListViewHolder {
            val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
            return ChatListViewHolder(view)
        }
        //원래 코드
//        fun checkMsg(){
//            notifyDataSetChanged() //전체 새로고침
//            recyclerView?.scrollToPosition(comments.size -1)
//        }
//
        fun checkMsg(){
            fireDatabase.child("chatrooms").child(tripId.toString()).child("comments")
                .addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                    }
                    override fun onDataChange(snapshot: DataSnapshot) {
                        getMessageList()
                        notifyDataSetChanged()
                        recyclerView?.scrollToPosition(comments.size -1)
                    }
                })
        }
        override fun onBindViewHolder(holder: ChatListViewHolder, position: Int) {
            val member = nickname
            Log.d("chat","member>>>>>>>$member")
            holder.msgUsername.text = comments[position].username
            holder.msgTxt.text = comments[position].message
            holder.msgTime.text = comments[position].time
            holder.msgTime2.text = comments[position].time
//            if(comments[position].username.equals()){} //데이터 가져와서 본인 체크
            if(comments[position].username.equals(member)){ //본인 체크
//                holder.msgTxt.setBackgroundColor(Color.parseColor("#619CD6"))
                holder.msgTxt.setBackgroundResource(R.drawable.tv_co_pa)
                holder.msgTxt.setTextColor(Color.parseColor("#FFFFFF"))
                holder.msgProfileImg.visibility = View.INVISIBLE
                holder.msgUsername.visibility = View.VISIBLE
                holder.msgParentLayout.gravity = Gravity.RIGHT //최상위 부모 레이아웃 - 레이아웃 오른쪽
                holder.msgBgColor.gravity = Gravity.RIGHT
                holder.msgTime.visibility = View.GONE
                holder.msgTime2.visibility = View.VISIBLE
                holder.card.visibility = View.GONE
            }else{//상대
                holder.msgTxt.setBackgroundResource(R.drawable.s_chat_le)
                holder.msgProfileImg.visibility = View.VISIBLE
                holder.msgUsername.text = comments[position].username //채팅방에 있는 멤버들 중 채팅메시지-친구가 일치하는 거
                holder.msgUsername.visibility = View.VISIBLE
                holder.msgParentLayout.gravity = Gravity.LEFT
                holder.msgBgColor.gravity = Gravity.LEFT
                holder.msgTime.visibility = View.VISIBLE
                holder.msgTime2.visibility = View.GONE
                holder.card.visibility = View.VISIBLE

                val inetworkService =(this@ChatActivity?.applicationContext as MyApplication).networkService
                Log.d("cccccc", "${comments[position].username}")
                var oneUserCall = inetworkService.doGetOneUserByNickname(comments[position].username)

                oneUserCall.enqueue(object: Callback<User> {
                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        val profile_id = response.body()?.profile_id.toString()
                        Log.d("ccccc", "profiles/${profile_id}.jpg")

                        val imgRef = MyApplication.storage.reference.child("profiles/${profile_id}.jpg")
                        imgRef.downloadUrl.addOnCompleteListener{ task ->
                            if(task.isSuccessful){
                                if(profile_id != null){
                                    Glide.with(this@ChatActivity)
                                        .load(task.result)
                                        .into(holder.msgProfileImg)}

                            }
                        }

                    }
                    override fun onFailure(call: Call<User>, t: Throwable) {
                        Log.d("chatActivity", "실패")
                    }
                })
            }







        }

        override fun getItemCount(): Int {
            return comments.size
        }
        //ViewHolder - 각각에 데이터 붙이기
        inner class ChatListViewHolder(view: View): RecyclerView.ViewHolder(view){
            val msgTxt: TextView = view.findViewById(R.id.chatMsg)//
            val msgUsername: TextView = view.findViewById(R.id.chatMsgUsername)
            val msgProfileImg: ImageView = view.findViewById(R.id.chatProfile)
            val msgLayout: LinearLayout = view.findViewById(R.id.chatMsgLayout)
            val msgParentLayout: LinearLayout = view.findViewById(R.id.chatMagParentLayout)
            val msgTime: TextView = view.findViewById(R.id.chatMsgTime)
            val msgBgColor: LinearLayout = view.findViewById(R.id.chatMsgLayoutBg)
            val msgTime2: TextView = view.findViewById(R.id.chatMsgTime2)
            val card: CardView = view.findViewById(R.id.profilecard)
        }
    }










    //메시지 리스트 뿌리기
    private fun getMessageList(){
        fireDatabase.child("chatrooms").child(tripId.toString()).child("comments")
            .addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    comments.clear()
                    for(data in snapshot.children){
//                        val item = data.getValue<ChatModel.Comment>()
                        Log.d("chat", ".........$data")
                        //원래는 받아져야 하는데 Class
                        val item=data.getValue() as HashMap<String, Any?>

                        Log.d("chat", ">>>>>>>${item.get("time")}")
                        Log.d("chat", ">>>>>>>${item.get("message")}")
                        Log.d("chat", ">>>>>>>${item.get("username")}")
//                        comments.add(item.get())
//                        Log.d("chat", "item....."+item)
//                        val comment = Comment("", "", "")
                        val comment = Comment(item.get("username").toString(), item.get("message").toString(), item.get("time").toString())
                        comments.add(comment)
//                        comments.add(item.get().)
//                        comments.addAll(item as Collection<Comment>)
                        println(comments)
                    }
                    recyclerView?.layoutManager = LinearLayoutManager(this@ChatActivity)
                    chatAdapt = ChatListAdapter(comments)
                    recyclerView?.adapter = chatAdapt
//                    recyclerView?.addItemDecoration(DividerItemDecoration(this@ChatActivity,LinearLayoutManager.VERTICAL))
//                    chatAdapt.checkMsg() //전체 새로고침
                    Log.d("chat","comments..Suc...$comments")
                    recyclerView?.scrollToPosition(comments.size -1)
                }
            })
    }




    override fun onMenuItemClick(item: MenuItem?): Boolean {



        when (item?.itemId) { // 메뉴 아이템에 따라 동작 다르게 하기


            R.id.outMenu -> {
                var dialog_listener = DialogInterface.OnClickListener { dialog, which ->
                    val networkService = (applicationContext as MyApplication).networkService
                    val deleteUserinmeetingCall = networkService.doDeleteUserinmeeting(username, binding.meetingid.text.toString().toInt())
                    deleteUserinmeetingCall.enqueue(object: Callback<Int> {
                        override fun onResponse(call: Call<Int>, response: Response<Int>) {
                            Toast.makeText(this@ChatActivity, "모임을 나갔습니다.", Toast.LENGTH_SHORT)
                            finish()
                        }
                        override fun onFailure(call: Call<Int>, t: Throwable) {
                            Log.d("chatActivity", "실패")
                        }
                    })
                }


                AlertDialog.Builder(this@ChatActivity).run {
                    setTitle("모임 탈퇴")
                    setMessage("모임을 나가시겠습니까?")
                    setPositiveButton("아니오", null)
                    setNegativeButton("네", dialog_listener)
                    show()
                }
            }
            R.id.updateMenu -> {
                val intent = Intent(this@ChatActivity, UpdateMeetingActivity::class.java)
                intent.putExtra("meeting_id", meeting?.meeting_id)
                intent.putExtra("meeting_title", meeting?.meeting_title)
                intent.putExtra("meeting_content", meeting?.meeting_content)
                intent.putExtra("meeting_place_name", meeting?.meeting_place_name)
                intent.putExtra("meeting_place_imgurl", meeting?.meeting_place_imgurl)
                intent.putExtra("meeting_place_spot", meeting?.meeting_place_spot)
                intent.putExtra("start_date", meeting?.start_date)
                intent.putExtra("end_date", meeting?.end_date)
                startActivity(intent)
            }
            R.id.deleteMenu -> {
                var dialog_listener = DialogInterface.OnClickListener { dialog, which ->
                    val networkService = (applicationContext as MyApplication).networkService
                    val deleteUserinmeetingCall = networkService.doDeleteMeeting(binding.meetingid.text.toString().toInt())
                    deleteUserinmeetingCall.enqueue(object: Callback<Int> {
                        override fun onResponse(call: Call<Int>, response: Response<Int>) {
                            var mDatabase = FirebaseDatabase.getInstance();
                            var dataRef = mDatabase.getReference("chatrooms").child(meeting?.meeting_title!!)
                            dataRef.removeValue()

                            Toast.makeText(this@ChatActivity, "채팅방을 삭제했습니다.", Toast.LENGTH_SHORT)
                            finish()
                        }
                        override fun onFailure(call: Call<Int>, t: Throwable) {
                            Log.d("chatActivity", "실패")
                        }
                    })
                }


                AlertDialog.Builder(this@ChatActivity).run {
                    setTitle("채팅방 삭제")
                    setMessage("채팅방을 삭제하시겠습니까?")
                    setPositiveButton("아니오", null)
                    setNegativeButton("네", dialog_listener)
                    show()
                }
            }

        }
        return item != null // 아이템이 null이 아닌 경우 true, null인 경우 false 리턴
    }

    private fun showPopup(v: View) {
        val popup = PopupMenu(this, v) // PopupMenu 객체 선언

        popup.menuInflater.inflate(R.menu.chatadminmenu, popup.menu) // 메뉴 레이아웃 inflate
        popup.setOnMenuItemClickListener(this)
        popup.show() // 팝업 보여주기
    }

    private fun showPopup2(v: View) {
        val popup = PopupMenu(this, v) // PopupMenu 객체 선언



        popup.menuInflater.inflate(R.menu.chatmenu, popup.menu) // 메뉴 레이아웃 inflate
        popup.setOnMenuItemClickListener(this)
        popup.show() // 팝업 보여주기
    }










}