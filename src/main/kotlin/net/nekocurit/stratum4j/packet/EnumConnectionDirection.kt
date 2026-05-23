package net.nekocurit.stratum4j.packet

enum class EnumConnectionDirection(val description: String) {
    SERVER_BOUND("C -> S"),
    CLIENT_BOUND("S -> C");

    override fun toString() = this.description
}
