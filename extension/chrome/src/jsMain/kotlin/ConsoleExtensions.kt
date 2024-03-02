import kotlin.js.Console

inline fun Console.debug(vararg o: Any?) {
    asDynamic().debug(o)
}
