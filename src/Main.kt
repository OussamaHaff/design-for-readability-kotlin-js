import org.w3c.dom.HTMLButtonElement
import kotlin.browser.document
import kotlin.browser.window
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

fun main() {
    var currentState : State by Delegates.observable(Disconnected(), { _: KProperty<*>, oldValue: State, newValue: State ->
        window.alert("The new state is ${newValue::class.js.name}, before it was ${oldValue::class.js.name}")
    })


    val btnActionLogin = document.getElementById("btn-action-login") as HTMLButtonElement
    btnActionLogin.addEventListener("click", {
        currentState = currentState.consumeAction(Action.Login())
    })

    val btnActionRefresh = document.getElementById("btn-action-refresh") as HTMLButtonElement
    btnActionRefresh.addEventListener("click", {
        currentState = currentState.consumeAction(Action.Refresh())
    })

    val btnActionLogout = document.getElementById("btn-action-logout") as HTMLButtonElement
    btnActionLogout.addEventListener("click", {
        currentState = currentState.consumeAction(Action.Logout())
    })

    val btnReset = document.getElementById("btn-reset") as HTMLButtonElement
    btnReset.addEventListener("click", {
        currentState = Disconnected()
    })

}


interface State {

    fun consumeAction(action: Action) : State

}

sealed class Action {
    class Login : Action()
    class Logout : Action()
    class Refresh : Action()
}

class Disconnected : State {
    override fun consumeAction(action: Action): State {
        return when (action) {
            is Action.Login -> Connected()
            else -> this
        }
    }
}

class Connected : State {
    override fun consumeAction(action: Action): State {
        return when (action) {
            is Action.Logout -> Disconnected()
            is Action.Refresh -> Actualized()
            is Action.Login -> this
        }
    }
}

class Actualized : State {
    override fun consumeAction(action: Action): State {
        return when (action) {
            is Action.Logout -> Disconnected()
            is Action.Login -> this
            is Action.Refresh -> Actualized()
        }
    }
}