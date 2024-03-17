package com.tabka.backblogapp.viewmodels

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.tabka.backblogapp.network.repository.UserRepository
import com.tabka.backblogapp.ui.viewmodels.AuthViewModel
import com.tabka.backblogapp.util.DataResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

class AuthViewModelTest {
    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var auth: FirebaseAuth

    @Mock
    private lateinit var authResult: AuthResult

    @Mock
    private lateinit var currentUser: FirebaseUser

    private lateinit var authViewModel: AuthViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        authViewModel = AuthViewModel(auth, userRepository)

        // Mock behavior for auth.currentUser
        whenever(auth.currentUser).thenReturn(currentUser)
        whenever(currentUser.uid).thenReturn("fakeUserId")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testAttemptSignupShouldReturnSuccess() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        try {
            // Arrange
            val email = "email@email.com"
            val username = "fakeUser"
            val password = "password"

            val task: Task<AuthResult> = Tasks.forResult(authResult)
            whenever(auth.createUserWithEmailAndPassword(anyString(), anyString())).thenReturn(task)
            whenever(authResult.user).thenReturn(currentUser)
            whenever(userRepository.addUser(anyString(), anyString(), anyInt())).
            thenReturn(DataResult.Success(true))

            // Act
            val result = authViewModel.attemptSignup(email, username, password)

            // Assert
            Assert.assertEquals(result, Pair(true, ""))
        } finally {
            Dispatchers.resetMain()
        }
    }
}