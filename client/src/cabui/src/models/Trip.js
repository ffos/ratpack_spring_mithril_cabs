var m = require("mithril")

var Trip = {
    response: {
        data:[],
        error: ""
    },
    request: {
        medallions: "67EB082BFFE72095EAF18488BEA96050",
        date: "2013-12-30",
        ignoreCache: false,
    },
    ex_medallions: [
        "67EB082BFFE72095EAF18488BEA96050",
        "00FD1D146C1899CEDB738490659CAD30",
        "00377E15077848677B32CE184CE7E871",
        "000318C2E3E6381580E5C99910A60668",
        "00153E36140C5B2A84EA308F355A7925",
    ],
    query: function() {
        var parsedDate = Date.parse(Trip.request.date);
        var ignoreCacheParam = Trip.request.ignoreCache ? "ignoreCache": "";
        return m.request({
                    method: "GET",
                    url: "http://localhost:8999/api/cabs/trip_counts/"+ parsedDate +"/cab/" + Trip.request.medallions+"?"+ignoreCacheParam,
                })
                .then(function(result) {
                    Trip.response.data = result;
                })
                .catch(function(err){
                    Trip.response.error = err.message;
                });
    },
}

module.exports = Trip
