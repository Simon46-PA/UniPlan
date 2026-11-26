package mx.edu.uttt.uniplan.ui.login

import android.util.Patterns
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import mx.edu.uttt.uniplan.R
import mx.edu.uttt.uniplan.domain.model.PASSWORD_MIN_LENGTH
import mx.edu.uttt.uniplan.ui.auth.AuthUiState
import mx.edu.uttt.uniplan.ui.theme.UniPlanTheme

/** Registro de usuario nuevo (guarda en Room con la contraseña cifrada). */
@Composable
fun RegisterScreen(
    uiState: AuthUiState,
    onRegister: (name: String, email: String, password: String) -> Unit,
    onBackToLogin: () -> Unit,
    onFieldEdited: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirm by rememberSaveable { mutableStateOf("") }
    var nameError by rememberSaveable { mutableStateOf<Int?>(null) }
    var emailError by rememberSaveable { mutableStateOf<Int?>(null) }
    var passwordError by rememberSaveable { mutableStateOf<Int?>(null) }
    var confirmError by rememberSaveable { mutableStateOf<Int?>(null) }

    fun submit() {
        focusManager.clearFocus()
        @StringRes val nameErr = if (name.isBlank()) R.string.register_error_name_empty else null
        @StringRes val emailErr = when {
            email.isBlank() -> R.string.login_error_email_empty
            !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() -> R.string.login_error_email_invalid
            else -> null
        }
        @StringRes val passErr = if (password.length < PASSWORD_MIN_LENGTH) R.string.register_error_password_short else null
        @StringRes val confirmErr = if (confirm != password) R.string.register_error_password_mismatch else null
        nameError = nameErr
        emailError = emailErr
        passwordError = passErr
        confirmError = confirmErr
        if (nameErr == null && emailErr == null && passErr == null && confirmErr == null) {
            onRegister(name.trim(), email.trim(), password)
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(dimensionResource(R.dimen.spacing_large)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.register_title),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(dimensionResource(R.dimen.spacing_large)))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it; nameError = null; onFieldEdited() },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.register_name_label)) },
                isError = nameError != null,
                supportingText = { nameError?.let { Text(stringResource(it)) } },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it; emailError = null; onFieldEdited() },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.login_email_label)) },
                isError = emailError != null,
                supportingText = { emailError?.let { Text(stringResource(it)) } },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; passwordError = null; onFieldEdited() },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.login_password_label)) },
                isError = passwordError != null,
                supportingText = { passwordError?.let { Text(stringResource(it)) } },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next)
            )
            OutlinedTextField(
                value = confirm,
                onValueChange = { confirm = it; confirmError = null; onFieldEdited() },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.register_confirm_label)) },
                isError = confirmError != null,
                supportingText = { confirmError?.let { Text(stringResource(it)) } },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done)
            )

            uiState.errorRes?.let { errorRes ->
                Text(
                    text = stringResource(errorRes),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(dimensionResource(R.dimen.spacing_medium)))
            Button(
                onClick = { submit() },
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.button_height)),
                shape = RoundedCornerShape(dimensionResource(R.dimen.button_corner))
            ) {
                Text(stringResource(R.string.register_button), style = MaterialTheme.typography.labelLarge)
            }
            TextButton(onClick = onBackToLogin) {
                Text(stringResource(R.string.register_back_to_login))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RegisterScreenPreview() {
    UniPlanTheme {
        RegisterScreen(
            uiState = AuthUiState(),
            onRegister = { _, _, _ -> },
            onBackToLogin = {},
            onFieldEdited = {}
        )
    }
}
