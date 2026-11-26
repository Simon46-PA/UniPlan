package mx.edu.uttt.uniplan.ui.login

import android.util.Patterns
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch
import mx.edu.uttt.uniplan.R
import mx.edu.uttt.uniplan.ui.auth.AuthUiState
import mx.edu.uttt.uniplan.ui.theme.UniPlanTheme

@Composable
fun LoginScreen(
    uiState: AuthUiState,
    onLogin: (email: String, password: String) -> Unit,
    onCreateAccount: () -> Unit,
    onFieldEdited: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var emailError by rememberSaveable { mutableStateOf<Int?>(null) }
    var passwordError by rememberSaveable { mutableStateOf<Int?>(null) }

    fun validateEmail(): Boolean {
        @StringRes val error: Int? = when {
            email.isBlank() -> R.string.login_error_email_empty
            !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() -> R.string.login_error_email_invalid
            else -> null
        }
        emailError = error
        return error == null
    }

    fun validatePassword(): Boolean {
        val error: Int? = if (password.isEmpty()) R.string.login_error_password_empty else null
        passwordError = error
        return error == null
    }

    fun submit() {
        focusManager.clearFocus()
        val emailOk = validateEmail()
        val passwordOk = validatePassword()
        if (emailOk && passwordOk) onLogin(email.trim(), password)
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(dimensionResource(R.dimen.spacing_large)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Imagen / logotipo
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(dimensionResource(R.dimen.logo_container_size))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(R.drawable.ic_logo_uniplan),
                        contentDescription = stringResource(R.string.logo_description),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(dimensionResource(R.dimen.logo_icon_size))
                    )
                }
            }

            Spacer(Modifier.height(dimensionResource(R.dimen.spacing_large)))

            Text(
                text = stringResource(R.string.login_welcome),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(dimensionResource(R.dimen.spacing_small)))
            Text(
                text = stringResource(R.string.login_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(dimensionResource(R.dimen.spacing_large)))

            // Campo de correo
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = null
                    onFieldEdited()
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.login_email_label)) },
                leadingIcon = {
                    Icon(painterResource(R.drawable.ic_email), contentDescription = null)
                },
                isError = emailError != null,
                supportingText = { emailError?.let { Text(stringResource(it)) } },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            Spacer(Modifier.height(dimensionResource(R.dimen.spacing_small)))

            // Campo de contraseña con ícono de ojo
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = null
                    onFieldEdited()
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.login_password_label)) },
                leadingIcon = {
                    Icon(painterResource(R.drawable.ic_lock), contentDescription = null)
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter = painterResource(
                                if (passwordVisible) R.drawable.ic_visibility_off
                                else R.drawable.ic_visibility
                            ),
                            contentDescription = stringResource(
                                if (passwordVisible) R.string.login_hide_password
                                else R.string.login_show_password
                            )
                        )
                    }
                },
                isError = passwordError != null,
                supportingText = { passwordError?.let { Text(stringResource(it)) } },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { submit() })
            )

            // Error general (credenciales incorrectas, falla de base de datos...)
            uiState.errorRes?.let { errorRes ->
                Spacer(Modifier.height(dimensionResource(R.dimen.spacing_small)))
                Text(
                    text = stringResource(errorRes),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(dimensionResource(R.dimen.spacing_medium)))

            // Botón relleno
            Button(
                onClick = { submit() },
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.button_height)),
                shape = RoundedCornerShape(dimensionResource(R.dimen.button_corner))
            ) {
                Text(
                    text = stringResource(R.string.login_button),
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(Modifier.height(dimensionResource(R.dimen.spacing_small)))

            // Botones de texto
            TextButton(onClick = {
                if (validateEmail()) {
                    val message = context.getString(R.string.login_recovery_sent, email.trim())
                    scope.launch { snackbarHostState.showSnackbar(message) }
                }
            }) {
                Text(stringResource(R.string.login_forgot_password))
            }
            TextButton(onClick = onCreateAccount) {
                Text(stringResource(R.string.login_create_account))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    UniPlanTheme {
        LoginScreen(
            uiState = AuthUiState(),
            onLogin = { _, _ -> },
            onCreateAccount = {},
            onFieldEdited = {}
        )
    }
}
