package mx.edu.uttt.uniplan.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import mx.edu.uttt.uniplan.R
import mx.edu.uttt.uniplan.domain.model.User
import mx.edu.uttt.uniplan.ui.auth.AuthViewModel
import mx.edu.uttt.uniplan.ui.home.HomeScreen
import mx.edu.uttt.uniplan.ui.login.LoginScreen
import mx.edu.uttt.uniplan.ui.login.RegisterScreen
import mx.edu.uttt.uniplan.ui.schedule.ScheduleScreen
import mx.edu.uttt.uniplan.ui.schedule.ScheduleViewModel
import mx.edu.uttt.uniplan.ui.subjects.SubjectViewModel
import mx.edu.uttt.uniplan.ui.subjects.SubjectsScreen
import mx.edu.uttt.uniplan.ui.tasks.AddTaskDialog
import mx.edu.uttt.uniplan.ui.tasks.TaskViewModel
import mx.edu.uttt.uniplan.ui.tasks.TasksScreen
import java.time.LocalDate

private object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val MAIN = "main"
}

private enum class MainTab(@StringRes val label: Int, @DrawableRes val icon: Int) {
    HOME(R.string.tab_home, R.drawable.ic_home),
    TASKS(R.string.tab_tasks, R.drawable.ic_tasks),
    SUBJECTS(R.string.tab_subjects, R.drawable.ic_school),
    SCHEDULE(R.string.tab_calendar, R.drawable.ic_calendar)
}

/**
 * Raíz de la app. NavHost + composable() definen el grafo de navegación:
 * login -> registro -> main (con barra inferior de pestañas).
 * El AuthViewModel vive en la Activity y conserva la sesión al girar la pantalla.
 */
@Composable
fun UniPlanApp() {
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.LOGIN) {
        composable(Routes.LOGIN) {
            LaunchedEffect(authState.currentUser) {
                if (authState.currentUser != null) {
                    navController.navigate(Routes.MAIN) { popUpTo(Routes.LOGIN) { inclusive = true } }
                }
            }
            LoginScreen(
                uiState = authState,
                onLogin = authViewModel::login,
                onCreateAccount = {
                    authViewModel.clearError()
                    navController.navigate(Routes.REGISTER)
                },
                onFieldEdited = authViewModel::clearError
            )
        }
        composable(Routes.REGISTER) {
            LaunchedEffect(authState.currentUser) {
                if (authState.currentUser != null) {
                    navController.navigate(Routes.MAIN) { popUpTo(Routes.LOGIN) { inclusive = true } }
                }
            }
            RegisterScreen(
                uiState = authState,
                onRegister = authViewModel::register,
                onBackToLogin = {
                    authViewModel.clearError()
                    navController.popBackStack()
                },
                onFieldEdited = authViewModel::clearError
            )
        }
        composable(Routes.MAIN) {
            val user = authState.currentUser
            if (user == null) {
                // Sesión cerrada, cuenta eliminada o proceso reiniciado: de vuelta al inicio de sesión.
                LaunchedEffect(Unit) {
                    navController.navigate(Routes.LOGIN) { popUpTo(Routes.MAIN) { inclusive = true } }
                }
            } else {
                MainScreen(
                    user = user,
                    authErrorRes = authState.errorRes,
                    onAuthErrorShown = authViewModel::clearError,
                    onUpdateName = authViewModel::updateName,
                    onDeleteAccount = authViewModel::deleteAccount,
                    onLogout = authViewModel::logout
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreen(
    user: User,
    @StringRes authErrorRes: Int?,
    onAuthErrorShown: () -> Unit,
    onUpdateName: (String) -> Unit,
    onDeleteAccount: () -> Unit,
    onLogout: () -> Unit
) {
    val taskViewModel: TaskViewModel = hiltViewModel()
    val subjectViewModel: SubjectViewModel = hiltViewModel()
    val scheduleViewModel: ScheduleViewModel = hiltViewModel()
    val taskState by taskViewModel.uiState.collectAsStateWithLifecycle()
    val subjectState by subjectViewModel.uiState.collectAsStateWithLifecycle()
    val scheduleState by scheduleViewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var currentTab by rememberSaveable { mutableStateOf(MainTab.HOME) }
    var menuExpanded by remember { mutableStateOf(false) }
    var showAddTask by rememberSaveable { mutableStateOf(false) }
    var showAddSubject by rememberSaveable { mutableStateOf(false) }
    var showAddClass by rememberSaveable { mutableStateOf(false) }
    var showEditProfile by rememberSaveable { mutableStateOf(false) }
    var showDeleteAccount by rememberSaveable { mutableStateOf(false) }
    val noSubject = stringResource(R.string.task_no_subject)
    val noDate = stringResource(R.string.task_no_date)
    val today = remember { LocalDate.now().dayOfWeek.value }

    // Mensajes de error personalizados (cualquier ViewModel) en un Snackbar.
    val errorRes = taskState.errorRes ?: subjectState.errorRes ?: scheduleState.errorRes ?: authErrorRes
    val errorText = errorRes?.let { stringResource(it) }
    LaunchedEffect(errorRes) {
        if (errorText != null) {
            snackbarHostState.showSnackbar(errorText)
            taskViewModel.clearError()
            subjectViewModel.clearError()
            scheduleViewModel.clearError()
            onAuthErrorShown()
        }
    }

    Scaffold(
        topBar = {
            when (currentTab) {
                MainTab.HOME -> TopAppBar(
                    title = { Text(stringResource(R.string.home_greeting, user.name)) },
                    actions = {
                        Box {
                            FilledTonalIconButton(onClick = { menuExpanded = true }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_person),
                                    contentDescription = stringResource(R.string.home_profile)
                                )
                            }
                            DropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = { menuExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.home_edit_profile)) },
                                    onClick = {
                                        menuExpanded = false
                                        showEditProfile = true
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.home_delete_account)) },
                                    onClick = {
                                        menuExpanded = false
                                        showDeleteAccount = true
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.home_logout)) },
                                    onClick = {
                                        menuExpanded = false
                                        onLogout()
                                    }
                                )
                            }
                        }
                    }
                )
                MainTab.TASKS -> TopAppBar(title = { Text(stringResource(R.string.tasks_title)) })
                MainTab.SUBJECTS -> TopAppBar(title = { Text(stringResource(R.string.subjects_title)) })
                MainTab.SCHEDULE -> TopAppBar(title = { Text(stringResource(R.string.calendar_title)) })
            }
        },
        bottomBar = {
            NavigationBar {
                MainTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = currentTab == tab,
                        onClick = { currentTab = tab },
                        icon = { Icon(painterResource(tab.icon), contentDescription = null) },
                        label = { Text(stringResource(tab.label)) }
                    )
                }
            }
        },
        floatingActionButton = {
            val fabDescription = when (currentTab) {
                MainTab.TASKS -> R.string.tasks_add
                MainTab.SUBJECTS -> R.string.subjects_add
                MainTab.SCHEDULE -> R.string.schedule_add
                MainTab.HOME -> null
            }
            if (fabDescription != null) {
                FloatingActionButton(onClick = {
                    when (currentTab) {
                        MainTab.TASKS -> showAddTask = true
                        MainTab.SUBJECTS -> showAddSubject = true
                        MainTab.SCHEDULE -> showAddClass = true
                        MainTab.HOME -> Unit
                    }
                }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_add),
                        contentDescription = stringResource(fabDescription)
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (showAddTask) {
            AddTaskDialog(
                onDismiss = { showAddTask = false },
                onSave = { title, subject ->
                    taskViewModel.agregarTarea(title, subject.ifBlank { noSubject }, noDate)
                    showAddTask = false
                }
            )
        }
        if (showEditProfile) {
            EditProfileDialog(
                currentName = user.name,
                onDismiss = { showEditProfile = false },
                onSave = {
                    onUpdateName(it)
                    showEditProfile = false
                }
            )
        }
        if (showDeleteAccount) {
            AlertDialog(
                onDismissRequest = { showDeleteAccount = false },
                title = { Text(stringResource(R.string.delete_account_title)) },
                text = { Text(stringResource(R.string.delete_account_message)) },
                confirmButton = {
                    TextButton(onClick = {
                        showDeleteAccount = false
                        onDeleteAccount()
                    }) { Text(stringResource(R.string.dialog_delete)) }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteAccount = false }) {
                        Text(stringResource(R.string.dialog_cancel))
                    }
                }
            )
        }
        when (currentTab) {
            MainTab.HOME -> HomeScreen(
                tasks = taskState.tasks,
                todayClasses = scheduleState.classes.filter { it.dayOfWeek == today },
                onToggleTask = taskViewModel::alternarCompletada,
                onSeeAllTasks = { currentTab = MainTab.TASKS },
                onSeeAllClasses = { currentTab = MainTab.SCHEDULE },
                contentPadding = padding
            )
            MainTab.TASKS -> TasksScreen(
                tasks = taskState.tasks,
                onToggleTask = taskViewModel::alternarCompletada,
                onDeleteTask = taskViewModel::eliminarTarea,
                onUndoDelete = taskViewModel::deshacerEliminar,
                snackbarHostState = snackbarHostState,
                contentPadding = padding
            )
            MainTab.SUBJECTS -> SubjectsScreen(
                subjects = subjectState.subjects,
                showAddDialog = showAddSubject,
                onAddDialogDismiss = { showAddSubject = false },
                onSave = subjectViewModel::guardarMateria,
                onDelete = subjectViewModel::eliminarMateria,
                contentPadding = padding
            )
            MainTab.SCHEDULE -> ScheduleScreen(
                classes = scheduleState.classes,
                subjects = scheduleState.subjects,
                showAddDialog = showAddClass,
                onAddDialogDismiss = { showAddClass = false },
                onSave = scheduleViewModel::guardarClase,
                onDelete = scheduleViewModel::eliminarClase,
                contentPadding = padding
            )
        }
    }
}

@Composable
private fun EditProfileDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var name by rememberSaveable { mutableStateOf(currentName) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.profile_dialog_title)) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.register_name_label)) },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = { onSave(name) }, enabled = name.isNotBlank()) {
                Text(stringResource(R.string.dialog_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.dialog_cancel)) }
        }
    )
}
