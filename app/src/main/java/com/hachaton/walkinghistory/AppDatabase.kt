package com.hachaton.walkinghistory

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Place::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun placeDao(): PlaceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "walking_history_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database.placeDao())
                    }
                }
            }

            private suspend fun populateInitialData(dao: PlaceDao) {
                val samplePlaces = listOf(
                    Place(
                        name = "C-13 (Zintegrowane Centrum Studenckie, czyli \„Serowiec\”)\n",
                        description = "Rys historyczny: Oddany do użytku w 2007 roku budynek miał być nowoczesnym, integrującym sercem kampusu. Jego projekt (stworzony przez biuro Manufaktura 1) celowo łamał nudne, kanciaste standardy – stąd te słynne, okrągłe okna.\n",
                        narration = "Narracja: Z zewnątrz wygląda, jakby kosmici zgubili podczas pikniku gigantyczny blok ementalera i postanowili zostawić go nad brzegiem Odry. Wchodzisz do środka z naiwnym planem szybkiego wydrukowania notatek. Jednak nieregularne rozmieszczenie okien błyskawicznie zaburza twój błędnik. Schody zdają się prowadzić jednocześnie w górę i w dół (niczym na obrazach Eschera), a ty zaczynasz czuć się jak mysz w skomplikowanym eksperymencie psychologicznym.\n",
                        scene = "Scenka z życia wzięta:\n" +
                                "(Korytarz na 2. piętrze, które okazuje się być półpiętrem)\n" +
                                "– Przepraszam, wiesz jak dojść do sali 2.17? – pyta spocony pierwszoroczniak, trzymając w ręku wydruk planu zajęć.\n" +
                                "– Stary, ja tu szukam wyjścia od wtorku – odpowiada blady student o pustym spojrzeniu. – Byłem na parterze, wszedłem po schodach na górę i wylądowałem w piwnicy. Masz może powerbanka? I trochę wody?\n",
                        latitude = 51.1073338,
                        longitude = 17.0593010
                    ),
                    Place(
                        name = "SKS (Strefa Kultury Studenckiej)\n",
                        description = "Rys historyczny: Otwarty pod koniec 2012 roku budynek stanął w miejscu dawnej, obskurnej stołówki. Drewniano-szklana bryła, wkomponowana w otoczenie starych dębów, miała stworzyć nowoczesną przestrzeń do jedzenia, odpoczynku i szeroko pojętej studenckiej \"kultury\".\n",
                        narration = "Narracja: Nazwa sugeruje wyrafinowane debaty o sztuce, ale w rzeczywistości „kultura” objawia się tu głównie w sposobie, w jaki tłum potrafi się bezszelestnie przepychać w epickiej bitwie o jedzenie. Wchodzisz zwabiony zapachem taniej kawy i obiadu, a natychmiast trafiasz w sam środek surwiwalu. To mityczny wodopój głodnych inżynierów przypominający piękny, drewniany hangar, gdzie instynkty pierwotne biorą górę nad wiedzą z wytrzymałości materiałów.\n",
                        scene = "Scenka z życia wzięta:\n" +
                                "(Okolice centralnych stolików, godzina 13:15, szczyt głodu)\n" +
                                "– Ty, patrz! – syczy chłopak w bluzie W-4 do swojego kolegi, wskazując wzrokiem stolik pod oknem. – Ten gość zaraz będzie odnosił tacę. Zróbmy manewr oskrzydlający.\n" +
                                "– Czekaj. Rzucę mu pod nogi notatki z mechaniki płynów dla zmyłki. Ty rzucasz plecak na krzesło, ja osłaniam tyły i idę w bój o ostatniego schabowego. Tylko nie zepsuj tego, nie jadłem od wczoraj!\n",
                        latitude = 51.10743,
                        longitude = 17.05706
                    ),
                    Place(
                        name = "A-1 (Gmach Główny, czyli Inżynierski Hogwart)",
                        description = "Rys historyczny: Zbudowany w latach 1905–1911 dla ówczesnej Królewskiej Wyższej Szkoły Technicznej (Königliche Technische Hochschule Breslau) według projektu Richarda Plüddemanna. Ta potężna, neorenesansowa perła od początku miała być majestatyczną świątynią nauki reprezentującą prestiż uczelni.",
                        narration = "Narracja: Serce politechniki i jednocześnie architektoniczny trójkąt bermudzki. Wchodzisz głównym wejściem pełen optymizmu, wjeżdżasz zabytkową windą paternoster (zanim jej zakazano, a potem znowu włączono w formie atrakcji), skręcasz w lewo, idziesz prosto korytarzem... i nagle orientujesz się, że jesteś w innym budynku, na innym piętrze, a numeracja sal została zaprojektowana przez generator liczb pseudolosowych. Korytarze są tu tak długie, że na jednym końcu widać krzywiznę Ziemi.\n",
                        scene = "Scenka z życia wzięta:\n" +
                                "(Gdzieś pomiędzy skrzydłem A, B a magicznym przejściem do C, półmrok)\n" +
                                "[Student 1. roku, bliski łez, wpatruje się w mapę kampusu na telefonie]:\n" +
                                "– Przepraszam... gdzie jest sala 329a? Szedłem prosto korytarzem, nagle z drugiego piętra zrobiło się czwarte, potem zmienił się budynek na A-2, a skończyłem przed drzwiami z napisem \"Stowarzyszenie Weteranów Lutowania\"...\n" +
                                "[Student 5. roku, popija zimną kawę z automatu, zapatrzony w dal z mądrością mnicha]:\n" +
                                "– Młody, sala 329a nie istnieje w trójwymiarowej euklidesowej przestrzeni. Musisz pójść do starej klatki schodowej, stanąć na co trzecim stopniu, zamknąć oczy i pomyśleć o całkach podwójnych. Wtedy ściana się otworzy. Powodzenia.\n",
                        latitude = 51.10729,
                        longitude = 17.06012
                    )
                )
                dao.insertAll(samplePlaces)
            }
        }
    }
}
