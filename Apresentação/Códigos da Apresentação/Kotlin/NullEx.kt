fun NullEx(){
    var nome: String? = null
    println(nome?.length ?: 0) // imprime 0 se nome for nulo
}

fun main() {
    NullEx()
}