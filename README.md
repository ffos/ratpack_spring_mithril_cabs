# Cabs Client and Server

## Installation

The dump file `cab-data.sql` was specific to mysql, and since there were issues
importing it to h2 in-mem db, I just setup a local mysql environment to host data

### MySql Settings
You can modify `server/src/main/resources/application.properties` to match your
local settings or make changes as follows:

* Create mysql: db: `simplecab`
* Create user: `dev` with no password
* Grant access-rights to `dev` for `simplecab` as root
* Import `cab-data.sql` into this db

## Other
* Ensure you have Java 8+
* Ensure you have npm (or will be downloaded)
* Ensure you have port `8999` not in use
* Ensure you have a default browser set to open html files
** Client code has been tested in Chrome only
* Install `curl` or `ab` (apache benchmark) tools if not already present for testing

## Run
Execute the following from project dir:

`./gradlew run`

## Design choices

Things are kept fairly simple intentionally. Depending on spring and jdbc-template instead of jpa.
Server is in `Ratpack` that uses spring for DI.
Client is small fast `mithril-js` built with npm and webpack


## Simple test
Run the following from command line:

`curl -i XGET http://localhost:8999/api/cabs/trip_counts/1388408400000/cab/,,00377E15077848677B32CE184CE7E871,,000318C2E3E6381580E5C99910A60668,00153E36140C5B2A84EA308F355A7925,?ignoreCache`
`curl -i XGET http://localhost:8999/api/cabs/trip_counts/1388408400000/cab/,,00377E15077848677B32CE184CE7E871,,000318C2E3E6381580E5C99910A60668,00153E36140C5B2A84EA308F355A7925,`

`ab -c 10 -n 10 http://localhost:8999/api/cabs/trip_counts/1388408400000/cab/,,00377E15077848677B32CE184CE7E871,,000318C2E3E6381580E5C99910A60668,00153E36140C5B2A84EA308F355A7925,?ignoreCache`
`ab -c 10 -n 1000 http://localhost:8999/api/cabs/trip_counts/1388408400000/cab/,,00377E15077848677B32CE184CE7E871,,000318C2E3E6381580E5C99910A60668,00153E36140C5B2A84EA308F355A7925,`





