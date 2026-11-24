rootProject.name = "soa-itmo"

include(
    ":lib:contract",
    ":lib:query",
    ":services:movie-web-service",
    ":services:movie-ejb-service",
    ":services:oscar-service",
)
