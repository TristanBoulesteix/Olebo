package fr.olebo.domain.models

enum class ElementSize(val value: Int) {
    XS(30), S(60), M(120), L(200), XL(300), XXL(400);

    companion object {
        inline val DEFAULT
            get() = S
    }
}