package jdr.exia.update

fun runJar(path: String, vararg args: String) =
    Runtime.getRuntime().exec("java -jar $path ${args.joinToString(" ")}")!!