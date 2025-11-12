package com.example.app_clinica_atl.data.repositoryTest.fakes

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.app_clinica_atl.data.local.user.UserDao
import com.example.app_clinica_atl.data.local.user.UserEntity
import kotlinx.coroutines.flow.Flow

class FakeUserDAOTest  : UserDao{
    private val data = mutableListOf<UserEntity>()

    override suspend fun insert(user: UserEntity){
        val nextId = (data.maxOrNull {it.id ?: 0} ?: 0) + 1
        data.add(user.copy(id = nextId))
        return nextId.toLong()
    }


    override suspend fun count(): Int{
        return data.size
    }

    
    override fun getAll(): List<UserEntity?> {
        return data.sortedBy { it.id ?: 0 }
    }


    //todo no alcancé a ver el tipo del email
    override fun getbyEmail():  {
        return data.sortedBy { it.email == email }
    }


}



