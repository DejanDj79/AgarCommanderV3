# Ciljna struktura nove AgAR aplikacije

Status: predlog informacione arhitekture zasnovan na postojecoj aplikaciji i
stvarnim interfejsima robota

## 1. Cilj

Nova aplikacija ne treba da kopira raspored stare aplikacije. Treba da sacuva
sve postojece funkcije, ali da ih organizuje prema poslu koji operator obavlja:

- upravljanje robotom i aktivnom misijom;
- priprema i uredjivanje misija;
- mapiranje, lokalizacija i autonomni rad;
- upravljanje robotom i prikljucnom masinom;
- pregled gresaka i stanja sistema.

Aplikacija ostaje landscape-first i prilagodjena SIYI MK32 ekranu. Poslednji
profil veze moze da se povezuje automatski, tako da poseban prazan Connect ekran
nije potreban. Kada veza nije dostupna, aplikacija ostaje na poslednjem ekranu,
jasno prikazuje offline stanje i onemogucava samo komande koje zahtevaju robot.

### Startup i loader

Na pokretanju se prikazuje kratak full-screen loader sa AgAR identitetom i
jednostavnim indikatorom napretka. U tom periodu aplikacija:

- ucitava lokalnu konfiguraciju i poslednji profil veze;
- inicijalizuje lokalne podatke i komunikacioni sloj;
- pokrece povezivanje sa robotom u pozadini;
- priprema pocetno stanje ekrana.

Loader ceka samo lokalnu inicijalizaciju aplikacije. Ne sme neograniceno da
ceka robota niti da blokira ulazak u aplikaciju kada robot nije dostupan. Posle
inicijalizacije uvek se otvara `Operate`, dok se pokusaj povezivanja nastavlja u
pozadini. Globalni status prikazuje `Connecting`, `Online` ili `Offline`.

U prvoj UI fazi komunikacija jos nije funkcionalna. Mock stanje omogucava da se
pregledaju svi ekrani i stanja bez robota. Development/preview konfiguracija
treba da omoguci izbor mock `Online`, `Offline`, `Warning` i `Error` stanja, ali
te kontrole ne smeju biti deo produkcionog operaterskog interfejsa.

## 2. Glavna navigacija

Stalni levi navigation rail sadrzi sedam glavnih odredista:

1. `Operate`
2. `Missions`
3. `Maps & SLAM`
4. `PTO`
5. `Robot`
6. `Commands`
7. `Diagnostics`

`Settings` i profil veze nalaze se na dnu rail-a. `Operate` se otvara po
defaultu nakon pokretanja aplikacije.

Na vrhu svakog ekrana postoji kompaktan status bar:

- Local/VPN/offline veza;
- robot online i system ready;
- GPS/RTK status;
- trenutni operativni rezim;
- aktivna misija;
- brake status;
- broj aktivnih upozorenja;
- baterija robota.

Brake/Stop pristup mora biti vidljiv sa svakog operaterskog ekrana. Aplikacija
ne sme da predstavlja softversku brake komandu kao zamenu za fizicki E-stop.
Remote VPN profil je podrazumevano monitoring profil i ne sme da omoguci rucnu
voznju bez posebnog robot-side odobrenja.

## 3. Operate

Ovo je glavni ekran za svakodnevni rad.

### Glavni prostor

- leva kolona sa aktivnom misijom, napretkom i start/pause/stop tokom;
- velika mapa desno sa robotom, aktivnom rutom i waypoint-ima;
- status lokalizacije i GPS/RTK kvaliteta;
- ime misije, broj waypoint-a i poseceni waypoint-i;
- jasan `Start mission` / `Stop mission` tok;
- upozorenja na prepreke, gubitak lokalizacije i safety blokade.

Svetla i buzzer nalaze se u `Commands`, PTO kontrole na zasebnom `PTO`
ekranu, a brake ostaje globalno dostupan u statusnoj traci.

### Rucna voznja

Touch joystick-i se otvaraju kao namenski drive panel, a ne kao stalni deo
ekrana. Panel sadrzi:

- napred/nazad joystick;
- tank/skretanje joystick;
- robot-confirmed maksimalna brzina i maksimalna brzina skretanja;
- posebna osetljivost touch joystick-a;
- dead-man stanje i aktivni izvor komande;
- brake.

Postojeci nezavisni MK32 RC put ostaje nepromenjen. Aplikacija ne preuzima
fizicke joystick-e u prvoj verziji. Robot mora da arbitira RC, aplikaciju i
autonomnu navigaciju kroz ista safety pravila.

Zajednicka ogranicenja brzine nalaze se u `Commands` i vaze i za rucnu i za
autonomnu voznju. Fizicke MK32 komande ostaju nezavisne, dok aplikacija
prikazuje vrednosti koje je robot potvrdio i ne prepisuje ih pri povezivanju.

## 4. Missions

Stari `Load Mission` i `Edit Mission` postaju jedan workspace sa dva taba.

### Library

- lista misija sa pretragom i refresh komandom;
- ucitavanje izabrane misije;
- detalji: broj waypoint-a, poseceni waypoint-i, datum i vezana mapa;
- reset visited uz potvrdu;
- jasno razlikovanje misije izabrane u listi i misije aktivne na robotu.

### Editor

- mapa i trenutna pozicija robota;
- lista waypoint-a;
- add waypoint;
- reverse;
- save/save as;
- load u editor;
- upozorenje za nesacuvane izmene.

Ucitavanje u editor i aktiviranje misije za voznju ostaju dve odvojene radnje.

## 5. Maps & SLAM

SLAM nije deo opsteg Settings ekrana. Dobija poseban workspace jer obuhvata
rezime, mape, lokalizaciju i kasnije autonomnu voznju.

Operator vidi cetiri korisnicka rezima:

- `GPS` - postojeci normalni rad, interno SLAM `off`;
- `Mapping` - pravljenje nove mape;
- `Localization` - rad sa izabranom mapom;
- `Autonomous` - lokalizacija i Nav2 voznja, kada kompletan tok bude spreman.

Ekran sadrzi:

- trenutni i trazeni rezim;
- stanje prelaza i razlog odbijanja;
- aktivnu mapu;
- listu mapa i kompletnost map bundle-a;
- start/stop mapping;
- save, activate i kasnije delete mape;
- trajanje mapiranja;
- localization state i quality;
- Lidar, ZED, IMU, TF, map i costmap health;
- readiness za autonomnu voznju.

Izbor `AMCL` ili `slam_toolbox localization` nije korisnicki rezim. Nalazi se u
Service/Advanced delu. `standard` i `zed_constrained` mapping profili takodje su
servisna podesavanja.

### Trenutno ogranicenje

U trenutnom kodu se `SLAM_MODE` bira staticki u
`agar_jetson_launch.py`. Postoje `off`, `mapping`, `localization`/AMCL i
`localization_slam_toolbox`, ali ne postoji runtime manager koji bezbedno menja
rezim. Zbog toga aplikacija jos ne sme direktno da restartuje nodove, menja
launch fajl ili prikazuje promenu rezima kao gotovu.

Pre aktiviranja ovih kontrola potreban je robot-side SLAM mode manager sa
statusom, validacijom, acknowledgement-om i rollback-om. Do tada ekran moze da
prikazuje samo trenutni status i SLAM health.

Postojeci ROS2 `slam_map_manager` vec ima servise:

- `/slam_map_manager/list_maps`;
- `/slam_map_manager/save_map`;
- `/slam_map_manager/set_active_map`.

Oni trenutno nisu dostupni direktnom ROS1 ROSBridge klijentu na RPi-ju, pa ih
runtime manager/gateway mora izloziti aplikaciji kao jedan kontrolisan API.

## 6. Robot

Ovaj workspace ima dva taba: `Actuators` i `Safety`.

### Actuators

Zadrzava sve funkcije starog Clearance ekrana:

- cetiri trenutne i ciljne pozicije;
- send position;
- Drive, Center, Up i Down preset;
- hold-to-run Up/Down;
- initialization;
- attachment mode.

Pored toga prikazuje drive safety stanje:

- `Safe to drive`;
- `Drive locked - robot is raised`;
- `Override active`;
- `Driving above height limit`.

Blokada visine nije normalan on/off prekidac za operatora. Robot-side safety
policy treba da bude ukljucen u konfiguraciji. Eventualni `Temporary drive
override` pripada Service rezimu, zahteva eksplicitnu potvrdu i mora biti
vremenski ogranicen.

Trenutni kod objavljuje:

- `/actuator_drive_lock` (`std_msgs/Bool`);
- `/actuator_drive_safety_status` (`Int32MultiArray`), gde je element 0
  override, a element 1 voznja iznad limita;
- prima `/actuator_drive_override` (`std_msgs/Bool`).

Pre izlaganja override-a aplikaciji treba dodati robot-side timeout. Trenutni
Bool override ostaje aktivan dok neko eksplicitno ne posalje `false`, sto nije
dovoljno bezbedno za mobilni UI. Takodje, integrisani `pid.py` trenutno koristi
default `actuator_drive_lock_enabled=false`, a launch ga ne postavlja. To mora
da se razresi i objavi kao deo statusa, jer samo `/actuator_drive_lock=false`
ne govori da li je robot bezbedan ili je cela funkcija iskljucena.

## 7. PTO

- start/stop i izbor brzine;
- rpm, struja, temperatura i telemetry age/quality;
- `PTO speed protection` status;
- trenutni derate faktor u procentima;
- razlog usporavanja ili stopa;
- istorija kratkog trenda kada bude potrebna.

Postojeci fuzzy transport vec podrzava `on`, `off` i `status` preko
`/fuzzy_command` i `/fuzzy_command_reply`. U normalnom UI-ju se ne koristi
termin `fuzzy controller`, vec razumljiv naziv `PTO speed protection`.

Iskljucivanje te zastite je safety bypass: zahteva potvrdu i ostavlja trajno
vidljivo upozorenje dok je bypass aktivan. Parametri krive, strujni pragovi i
timeout-i pripadaju samo Service/Advanced rezimu.

## 8. Commands

- zajednicki maksimum brzine za rucnu i autonomnu voznju;
- zajednicki maksimum brzine skretanja;
- potvrda aktivnog izvora kontrole i sinhronizacije MK32 dugmadi;
- svetla;
- buzzer mode.

Buzzer se prikazuje kao jedan segmented control:

- `Off`;
- `Reverse`;
- `Forward`;
- `Both`.

Poseban enable prekidac nije potreban jer je `Off` vec iskljuceno stanje.
Postojeci `movement_buzzer_client.py` podrzava upravo ova cetiri moda i po
defaultu koristi `Reverse`.

Trenutno nema cistog ROS status/command ugovora za aplikaciju. Kod cita ROS
parametre i salje HTTP komande buzzer serveru. Pre mobilne integracije treba
dodati ROS service ili command/reply adapter koji vraca success, applied mode,
server availability i poruku o gresci. Aplikacija ne treba direktno da poziva
HTTP buzzer uredjaja.

Telegram buzzer helper trenutno sadrzi hardkodovan bot token. Pre daljeg rada
taj token treba opozvati/rotirati i ucitavati iz environment ili secret
konfiguracije. Taj problem nije deo Android UI-ja, ali mora biti zatvoren pre
objavljivanja ili deljenja robot koda.

## 9. Diagnostics

Jedan ekran objedinjuje:

- system health;
- SLAM health i localization jump upozorenja;
- Medkit faults;
- ZED, Lidar, GPS/RTK i odometry stanje;
- PTO telemetry i fuzzy upozorenja;
- actuator safety dogadjaje;
- poslednje robot notifications.

Prvi prikaz je kratak operator summary. Detalji, timestamp-i i tehnicki nazivi
otvaraju se na zahtev. Aktivna kriticna greska uvek se vidi u globalnom status
baru, bez obzira na otvoreni ekran.

## 10. Settings i Service rezim

Normalni Settings sadrzi:

- profile veze;
- Local Control i VPN Monitor pravila;
- robot host i ROSBridge/gateway port;
- jedinice, izgled i korisnicke preference.

Service rezim je odvojen i zasticen:

- SLAM backend i mapping profil;
- fuzzy tuning, samo kada bude odobreno;
- actuator temporary override;
- senzorska i transportna dijagnostika;
- napredni connection podaci.

Normalan operator ne vidi tuning parametre koji mogu da promene safety
ponasanje robota.

## 11. Vizuelni pravac

- taman neutralan interfejs sa svetlim tekstom;
- zelena samo za potvrdeno dobro stanje, zuta za upozorenje, crvena za stop i
  gresku;
- flat sekcije i razdelnici umesto kartice unutar kartice;
- 48-56 dp minimalni touch target;
- ikone za jasne komande, tekst uz safety i mode komande;
- segmented controls za rezime i buzzer mode;
- toggle samo za stvarno binarna stanja;
- vrednost i jedinica uvek zajedno;
- offline podaci su oznaceni kao stale, nikad prikazani kao aktuelni;
- komanda ima stanja sending, accepted, applied i failed.

## 10. Redosled implementacije

1. Napraviti app shell, globalni status bar i connection profile.
2. Preneti postojeci Navigation u `Operate`, bez promene robot ponasanja.
3. Spojiti Load i Edit Mission u `Missions`.
4. Preneti Clearance u `Robot > Actuators`.
5. Dodati PTO i postojeci fuzzy command/reply tok.
6. Dodati buzzer ROS adapter i zatim buzzer UI.
7. Ucvrstiti actuator lock status i vremenski ogranicen override pre override UI-ja.
8. Dodati read-only SLAM status i health.
9. Implementirati robot-side SLAM mode manager.
10. Tek zatim aktivirati runtime SLAM komande i Autonomous rezim.

Ovaj redosled cuva postojeci nacin rada robota: prvo se nova aplikacija pravi kao
kompatibilan klijent, a nove kontrole se ukljucuju tek kada robot-side ugovor i
safety pravila budu spremni.
