package ovh.gabrielhuav.flasklogin.data.api

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("register")
    suspend fun register (@Body request: RegisterRequest): Response<AuthResponse>

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("tasks")
    suspend fun getTasks(): Response<List<TaskDto>>

    @POST("tasks")
    suspend fun createTask(@Body task: TaskDto): Response<TaskDto>

    @PUT("tasks/{id}")
    suspend fun updateTask(@Path("id") id: Int, @Body task: TaskDto): Response<TaskDto>

    @DELETE("tasks/{id}")
    suspend fun deleteTask(@Path("id") id: Int): Response<AuthResponse>
}

object RetrofitClient {
    // 10.0.2.2 apunta al localhost de la PC desde el emulador Android
    private const val BASE_URL = "http://10.0.2.2:5000/"

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}