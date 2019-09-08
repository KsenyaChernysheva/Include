package com.example.include.domain.user

import com.example.include.data.user.User
import com.vk.sdk.api.*
import com.vk.sdk.api.model.VKApiUser
import com.vk.sdk.api.model.VKList

class UserModel {
    var curUser: User? = null
    var curUserPic: String? = null

    fun loadUserPic() {
        val yourRequest = VKApi.users()
            .get(VKParameters.from(VKApiConst.USER_IDS, curUser?.screen_name, VKApiConst.FIELDS, "photo_400_orig"))

        yourRequest.executeSyncWithListener(object : VKRequest.VKRequestListener() {
            override fun onComplete(response: VKResponse?) {
                super.onComplete(response)
                val usersArray = response?.parsedModel as VKList<VKApiUser>
                for (userFull in usersArray) {
                    curUserPic = userFull.photo_400_orig
                }
            }
        })
    }
}