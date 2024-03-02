
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import react.PropsWithClassName
import react.StateInstance
import react.useEffectOnce
import react.useState
import web.cssom.ClassName
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

var PropsWithClassName.classNameString: String
    get() = className?.toString().orEmpty()
    set(value) {
        className = ClassName(value)
    }

var <T> StateInstance<T>.value
    get() = component1()
    set(value) {
        component2()(value)
    }

inline fun <T : Any> useSuspendState(crossinline initialize: suspend CoroutineScope.() -> T): StateInstance<T?> {
    val data = useState(null as T?)

    useEffectOnce {
        val job = mainScope.launch {
            data.value = initialize()
        }
        cleanup {
            job.cancel()
        }
    }

    return data
}

private class DerivedState<T>(
    private val getValue: () -> T,
    private val updateValue: (T) -> Unit,
) : ReadWriteProperty<Any?, T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T = getValue()
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = updateValue(value)
}

fun <T> derivedState(
    getValue: () -> T,
    updateValue: (T) -> Unit,
): ReadWriteProperty<Any?, T> = DerivedState(getValue, updateValue)
