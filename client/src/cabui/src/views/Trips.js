var m = require("mithril")
var Trip = require("../models/Trip")

module.exports = {
    view: function() {
        var queryServer = function(e){
            Trip.query();
            e.preventDefault();
        };
        var form = m("form#req", [
            m("label[for=csv]", "Enter CSV"),
            m("input#csv[type=text]", {
                value: Trip.request.medallions,
                oninput: m.withAttr("value", function(value) {Trip.request.medallions = value})
            }),
            m("label[for=date]", "Enter Date"),
            m("input#date[type=text]", {
                value: Trip.request.date,
                oninput: m.withAttr("value", function(value) {Trip.request.date = value})
            }),
            m("", [
                m("label[for=ignore]", "Ignore Cache"),
                m("input#ignore[type=checkbox]", {
                    value: Trip.request.ignoreCache,
                    checked: Trip.request.ignoreCache,
                    onclick: m.withAttr("checked", function(value) {Trip.request.ignoreCache = value})
                }),
            ]),
            m("button[type=submit]",{onclick: queryServer}, "Submit"),
        ]);
        var spacer = m("", {style: "width:10px;height:60px;"});
        var hints = m("", [
            m("h2", "Example Medallions"),
            m("hr"),
            m(".examples", [
                m("ul", [
                    m("", Trip.ex_medallions.map(function(ex){
                            return m("li", ex);
                          })),
                ])
            ]),
        ]);
        var responseOk = m(".response", Trip.response.data.map(function(v){
            return m("ul.cards", [
                m("li.card", [
                    m(".cab", [
                        m("p","Medallion"),
                        m("p",v.medallion)
                    ]),
                    m(".trips", [
                        m("p","Count"),
                        m("p",v.trips)
                    ]),
                ])
            ]);
        }));
        var responseError = m(".error", Trip.response.error);
        var response = (Trip.response.error) ? responseError: responseOk;
        return m(".data.full-height", [
            m(".input", [
                form,
                spacer,
                hints
            ]),
            m(".output", [
                response
            ])
        ]);
    }
}
