# Ekrani postojece aplikacije

Ovaj dokument popunjavamo na osnovu postojece aplikacije. Njegova svrha je da
sacuvamo funkcionalni inventar, a ne da prepisemo stari UI. Inventar jos nije
zavrsen jer glavni ekrani imaju dodatne podmenije koje treba pojedinacno
popisati.

Nova aplikacija mora da sacuva potrebne funkcije, ali moze imati potpuno
drugaciji izgled, navigaciju, grupisanje funkcija i broj ekrana. Postojeci
landscape raspored, podela pocetnog ekrana i polozaj ikona predstavljaju samo
referencu trenutnog stanja.

## Globalno ponasanje i povezivanje

- aplikacija radi samo u landscape orijentaciji;
- tablet se povezuje na Wi-Fi mrezu robota;
- bez te lokalne Wi-Fi veze aplikacija trenutno ne moze da koristi robot;
- ROSBridge adresa je `192.168.0.61`;
- ROSBridge port je `9090`;
- pun endpoint je `ws://192.168.0.61:9090`.

Pocetni ekran je podeljen na dve polovine:

- levo se nalaze ikone glavnih ekrana;
- ikone su disabled dok aplikacija nije povezana sa robotom;
- desno se nalaze dugmad `Connect` i `Disconnect`;
- nakon uspesnog povezivanja operaterski ekrani postaju dostupni.

Za novu aplikaciju lokalni robot Wi-Fi ostaje obavezni kompatibilni profil.
Adresa `192.168.0.61` i port `9090` su podrazumevane vrednosti, ali se cuvaju u
DataStore-u i mogu se menjati kroz Settings. Udaljena VPN/gateway veza kasnije
se dodaje kao poseban connection profil.

## Postojece funkcionalne grupe predstavljene kao glavni ekrani

Korisnickim opisom i ADB UI proverom aktivne MK32 aplikacije potvrdjeno je pet
trenutno vidljivih ikona:

- Navigation
- Load Mission
- Edit Mission
- Clearance
- Settings

U starijoj lokalnoj APK kopiji postoji i sesta ikona `Statistics`, koja vodi na
prazan placeholder ekran. APK izvucen iz aktivnog `com.coming.agarcontrol`
paketa i UI hijerarhija pokrenute aplikacije potvrdjuju da je ta opcija skrivena
na MK32. Nova aplikacija je zato ne prenosi kao postojecu funkciju.

Svaki od ovih ekrana ima dodatne podmenije ili podekrane. Njihov potpuni popis
ce biti dodat nakon opisa postojece aplikacije. Ovi nazivi ne odredjuju buduci
broj ekrana nove aplikacije.

## 1. Navigation

Glavni ekran za rad sa robotom.

### Gornji statusni red

Odmah ispod Android status bara prikazuju se:

- kvalitet GPS signala, odnosno GPS/RTK fix status;
- ime trenutno ucitane misije, ako je misija ucitana.

### Glavne komande

U gornjem desnom delu ekrana nalazi se pet ikona:

1. `Start Nav`
2. `Light`
3. `PTO`
4. `Drive`
5. `Settings`

Njihovo trenutno ponasanje:

- `Start Nav` pokrece izvrsavanje ucitane misije. Komanda ima smisla samo ako
  je misija prethodno ucitana.
- `Light` pali/gasi svetla na robotu. To odgovara postojecim komandama za
  `/bridge_light`.
- `PTO` otvara podmeni za prikljucnu masinu.
- `Drive` otvara kontrole za rucnu voznju robota.
- `Settings` otvara dodatni meni uz donju ivicu Navigation ekrana.

### PTO podmeni

PTO podmeni sadrzi:

- `PTO Start` - pokrece prikljucnu masinu;
- `PTO Stop` - zaustavlja prikljucnu masinu;
- `Low` - bira 200 rpm;
- `Medium` - bira 270 rpm;
- `High` - bira 540 rpm.

Prikazane vrednosti 200/270/540 rpm predstavljaju korisnicke brzine masine.
Analiza APK-a izvucenog iz aktivne MK32 aplikacije pokazala je da se na robot
salju interne komandne vrednosti `41`, `45` i `100`. Tacno mapiranje ostaje deo
transportnog sloja i ne treba da bude hardkodovano u UI komponentama.

### Drive podmeni

Rucna voznja koristi dva joysticka:

- levi joystick upravlja kretanjem napred i nazad;
- desni joystick upravlja skretanjem, odnosno tank okretanjem.

Za novu aplikaciju tek treba precizno potvrditi opseg vrednosti, ucestalost
slanja komandi, ponasanje pri pustanju joysticka i dead-man pravila.

### Fizicke kontrole operaterskog uredjaja

Aplikacija se najcesce koristi na kontroleru `SIYI MK32`, koji pored Android 9
ekrana ima fizicke komande:

- joystick-e za rucnu voznju robota;
- dugme `Brake On`;
- dugme `Brake Off`;
- dugme za pokretanje misije;
- dugme za zaustavljanje misije.

MK32 ima 16 fizickih/komunikacionih kanala. Prema proizvodjacevom podrazumevanom
mapiranju, cetiri ose joystick-a `J1` do `J4` koriste kanale 1 do 4, dok su
prekidaci i dodatna dugmad mapirani na ostale RC kanale.

ADB je potvrdio da je aktivna aplikacija `com.coming.agarcontrol`. APK izvucen
iz te instalacije ne sadrzi SIYI/MK32 SDK, Android `InputDevice`, `KeyEvent`,
`dispatchKeyEvent` niti `onGenericMotionEvent` obradu. Android input lista ne
prikazuje fizicke palice kao gamepad/joystick, a tokom provere nije radio nijedan
drugi AgAR Android proces. Fizicka voznja zato ne prolazi kroz aktivnu Android
aplikaciju, vec najverovatnije ide nezavisnim MK32 RC putem do robota.

Na uredjaju postoji i stariji paket `com.coming.agarcommander`, koji nije bio
aktivan i ciji se UI ne podudara sa korisnickim opisom. Taj paket ima genericku
Android gamepad obradu i moze da objavljuje `/cmd_vel`, ali to nije dokaz da taj
put koristi trenutni operaterski sistem.

Zbog toga nova aplikacija u prvoj verziji ne treba da preuzme fizicke
joystick-e niti da menja postojeci RC put. Touch joystick-i i ekranske komande
ostaju rezervni ulaz na telefonu ili tabletu. Sistem na robotu mora da uskladi
oba izvora kroz iste safety provere, ogranicenja brzine, stanje kocnice i stanje
misije.

U jednom trenutku samo jedan izvor sme da ima aktivnu kontrolu voznje. Fizicki
`Brake On` treba da ima najvisi prioritet, dok `Brake Off` ne sme sam da pokrene
kretanje. Ako aplikacija radi kroz VPN/remote monitoring rezim, njene touch
komande za voznju moraju biti blokirane. Nezavisni MK32 RC put ne prolazi kroz
VPN i ostaje pod robot-side arbitration i radio-link failsafe pravilima.

### Settings podmeni na Navigation ekranu

Meni se otvara uz samu donju ivicu ekrana i sadrzi:

- slider za podesavanje brzine kretanja robota;
- slider za podesavanje jacine skretanja, odnosno tank komande;
- dugme `Brake`, koje zaustavlja robota.

Ovaj podmeni predstavlja brza podesavanja voznje i ne mora u novoj aplikaciji
da bude isto organizovan kao glavni Settings ekran.

### Potvrdjeni statusi i podaci

- kvalitet GPS signala/fix status;
- ime ucitane misije;
- dostupnost `Start Nav` u odnosu na ucitanu misiju;
- trenutno stanje navigacije treba da bude vidljivo operatoru;
- stanje svetla, PTO-a i manual drive moda treba da bude jasno prikazano u
  novoj aplikaciji, cak i ako stara aplikacija za svako stanje nema poseban
  indikator.

### Otvorene provere za Navigation

- Da li `Start Nav` ikona tokom aktivne misije prelazi u `Stop Nav` i kako se
  zaustavlja aktivna navigacija.
- Tacni minimum, maksimum i jedinice oba slidera.
- Tacan ROS payload i frekvencija joystick komandi.
- Da li `Brake` samo salje stop brzinu, ukljucuje fizicku kocnicu ili radi oba.
- Koji vizuelni feedback postoji za svetlo, PTO i aktivnu navigaciju.
- Tacno mapiranje MK32 RC kanala za voznju, `Brake On`, `Brake Off`, `Start
  Mission` i `Stop Mission`.
- Kako vazdusna jedinica predaje RC kanale robotu i koji proces ih pretvara u
  komande kretanja, kocnice i navigacije.
- Tacno mapiranje sistemskih MK32 key uredjaja i da li ih neki robot-side ili
  proizvodjacki servis koristi izvan aktivne aplikacije.
- Pravilo preuzimanja kontrole izmedju MK32 RC puta i touch joystick-a.

### Napomene za novu aplikaciju

- Ovo je funkcionalni inventar, ne zahtev da pet komandi ostanu u istom uglu.
- Emergency stop/brake mora biti vrlo brzo dostupan i jasno odvojen od obicnih
  podesavanja.
- Start/Stop navigacije ne sme da zavisi od video stream-a.
- PTO brzine treba da budu definisane konfiguracijom/domain modelom, a ne
  direktno u UI kodu.

## 2. Load Mission

Ekran je podeljen u dve kolone: `Load Mission` levo i `Mission Details` desno.
Ovo je funkcionalni inventar postojece aplikacije; nova aplikacija ne mora da
zadrzi isti dvokolonski raspored na svakoj velicini ekrana.

### Leva kolona: Load Mission

- Padajuci meni prikazuje spisak misija koje su dostupne na RPi-ju.
- Misije se trenutno nalaze u folderu:

```text
/home/nvidia/ros_data/waypoints
```
- Dugme `Load` ucitava misiju koja je trenutno izabrana u padajucem meniju.

Izabrana misija i uspesno ucitana misija moraju biti odvojena stanja. Promena
izbora u meniju ne sme sama da promeni aktivnu misiju dok korisnik ne pritisne
`Load` i robot ne potvrdi uspesno ucitavanje.

### Desna kolona: Mission Details

Kolona prikazuje tri reda teksta:

- `Loaded mission` - ime trenutno ucitane misije;
- `Number of waypoints` - ukupan broj waypoint-a u ucitanoj misiji;
- `Visited waypoints` - broj vec posecenih waypoint-a.

Ispod detalja se nalazi dugme `Reset Visited`.

### Reset Visited tok

- Pritisak na `Reset Visited` prvo otvara dijalog za potvrdu.
- Dijalog jasno navodi da ce napredak trenutno ucitane misije biti resetovan.
- Tek nakon potvrde aplikacija salje komandu robotu.
- Otkazivanje dijaloga ne menja stanje misije.
- Posle uspesnog reseta `Visited waypoints` treba da prikaze vrednost koju vrati
  robot, ocekivano `0`.

### Potvrdjeni statusi i podaci

- Lista misija dostupnih na RPi-ju.
- Misija trenutno izabrana u padajucem meniju.
- Misija koja je stvarno ucitana na robotu.
- Ukupan broj waypoint-a.
- Broj posecenih waypoint-a.
- Rezultat ucitavanja i resetovanja misije.

### Napomene za novu aplikaciju

- `Load` i potvrda reseta treba da prikazu stanje izvrsavanja i da sprece
  dvostruko slanje komande.
- Prazna lista, nedostupan RPi, timeout i greska ucitavanja moraju imati jasna
  stanja umesto praznog ekrana.
- Nakon reconnect-a detalje aktivne misije treba ponovo procitati sa robota, a
  ne pretpostaviti poslednje lokalno stanje aplikacije.

Otvorene provere na postojecem sistemu:

- ROS service/topic koji vraca listu misija i format odgovora.
- ROS service/topic i odgovor za ucitavanje izabrane misije.
- ROS service/topic za `Reset Visited` i da li resetuje samo brojac ili i
  internu oznaku svakog waypoint-a.
- Da li lista treba da ima rucno osvezavanje i kako se sortira.
- Kako robot prijavljuje da je misija vec ucitana, neispravna ili prazna.

## 3. Edit Mission

Ekran je podeljen u dve kolone. Leva kolona sadrzi waypoint-e i komande za
kreiranje, ucitavanje i cuvanje misije, dok desna kolona prikazuje mapu i
trenutnu lokaciju robota.

Ovo je funkcionalni inventar postojece aplikacije. Nova aplikacija moze drugacije
organizovati prostor, ali mora da zadrzi vezu izmedju liste waypoint-a, njihove
putanje na mapi i stvarne lokacije robota.

### Desna kolona: mapa

- Prikazuje mapu.
- Prikazuje trenutnu lokaciju robota.
- Tokom voznje polozaj robota se azurira na mapi.
- Waypoint-i ucitane ili novokreirane misije treba da budu vidljivi na mapi kao
  uredjena putanja.

### Leva kolona: waypoint-i i komande

Na dnu leve kolone nalaze se dugmad:

- `Add`
- `Reverse`
- `Save`
- `Load`

Kada se misija ucita, iznad dugmadi se prikazuju svi njeni waypoint-i. Isti deo
ekrana koristi se za waypoint-e nove misije koja se kreira.

### Add

- `Add` dodaje novi waypoint u misiju koja se trenutno uredjuje.
- Na ovaj nacin operator moze da vozi robota i redom dodaje tacke nove misije,
  dok njegovo kretanje i nastala putanja vidi na mapi.
- Redosled dodavanja odredjuje redosled waypoint-a u misiji.

Tacan izvor koordinata koje `Add` upisuje, kao i da li se uz poziciju cuva
orijentacija ili dodatni podaci, treba potvrditi prema postojecem ROS interfejsu.

### Reverse

- `Reverse` obrce redosled cele misije.
- Poslednji waypoint postaje prvi, pretposlednji drugi i tako dalje, dok
  prvobitni prvi waypoint postaje poslednji.
- Lista waypoint-a i prikazana putanja na mapi moraju se odmah azurirati prema
  novom redosledu.

### Save dijalog

- Pritisak na `Save` otvara dijalog za cuvanje misije.
- Dijalog sadrzi polje za unos imena misije.
- Dugme `Save` u dijalogu cuva misiju pod unetim imenom.
- Dugme `Close` zatvara dijalog bez cuvanja.

Nova aplikacija mora prikazati rezultat cuvanja i upozoriti korisnika ako vec
postoji misija sa istim imenom ili ako ime nije dozvoljeno.

### Load Mission dijalog

- Pritisak na `Load` otvara dijalog za ucitavanje misije.
- Dijalog sadrzi padajuci meni sa spiskom dostupnih misija.
- Dugme `Load` u dijalogu ucitava izabranu misiju u editor.
- Dugme `Close` zatvara dijalog bez ucitavanja.
- Posle uspesnog ucitavanja svi waypoint-i misije prikazuju se u levoj koloni,
  a njihova putanja na mapi.

Ucitavanje misije u editor i aktiviranje misije za navigaciju treba tretirati
kao dve odvojene operacije, osim ako postojeci ROS interfejs izricito radi oba.

### Potvrdjeni statusi i podaci

- Trenutna lokacija robota.
- Waypoint-i misije koja se trenutno uredjuje i njihov redosled.
- Putanja misije na mapi.
- Ime ucitane ili nove misije, kada je dostupno.
- Rezultat ucitavanja i cuvanja.
- Da li postoje nesacuvane izmene.

### Napomene za novu aplikaciju

- Pre ucitavanja druge misije, zatvaranja editora ili gubitka veze treba
  upozoriti korisnika ako postoje nesacuvane izmene.
- `Save` i `Load` treba da budu onemoguceni dok je odgovarajuca operacija u
  toku, kako se zahtev ne bi poslao vise puta.
- Lista i mapa moraju oznacavati isti izabrani waypoint.
- Promene redosleda moraju biti prvo potvrdjene u lokalnom stanju editora, a
  trajno sacuvane tek komandom `Save`.

Otvorene provere na postojecem sistemu:

- Koji topic daje poziciju robota prikazanu na mapi i u kom koordinatnom
  sistemu.
- Da li `Add` koristi trenutnu GPS/RTK poziciju, lokalnu SLAM poziciju ili drugi
  izvor.
- Struktura jednog waypoint-a i format fajla misije.
- Da li editor podrzava izbor, brisanje ili rucno menjanje pojedinacnog
  waypoint-a.
- Da li `Reverse` menja samo redosled ili i orijentaciju/smer waypoint-a.
- Gde se misija fizicki cuva i kako se resava prepisivanje postojeceg imena.

## 4. Clearance

Ekran za upravljanje visinom robota preko cetiri aktuatora. Ovaj opis je
funkcionalni inventar postojece aplikacije, a ne obavezan raspored elemenata u
novoj aplikaciji. Vizuelna referenca:

```text
/home/agar/Pictures/Screenshots/Screenshot from 2026-05-17 22-07-40.png
```

### Raspored i polja aktuatora

- Header: Actuator settings.
- U prvom redu su `Front left` i `Front right`.
- U drugom redu su `Rear left` i `Rear right`.
- Za svaki od cetiri aktuatora prikazuju se:
  - trenutna pozicija u mm
  - polje za unos zeljene pozicije u mm

Slajderi i pojedinacni enable/disable prekidaci nisu deo trenutno potvrdjenog
funkcionalnog zahteva.

### Slanje pozicija i preset komande

- `Send position` salje robotu sve cetiri rucno unete zeljene pozicije.
- `Robot drive` salje preset od `175 mm` za sva cetiri aktuatora.
- `Robot center` salje preset od `125 mm` za sva cetiri aktuatora.
- `Robot up` salje preset od `250 mm` za sva cetiri aktuatora.
- `Robot down` salje preset od `0 mm` za sva cetiri aktuatora.

Vrednosti preseta treba drzati u konfiguraciji, a ne rasuti kao hardkodovane
brojeve kroz UI i transportni sloj.

### Hold-to-run komande

- `Up` podize robota sve dok korisnik drzi dugme pritisnuto.
- `Down` spusta robota sve dok korisnik drzi dugme pritisnuto.

Nova aplikacija mora poslati stop cim se dugme otpusti ili dodir bude otkazan.
Isto vazi pri gubitku fokusa, promeni ekrana, prelasku aplikacije u pozadinu ili
prekidu veze sa robotom.

### Initialization

- `Initialization` podize sva cetiri aktuatora do maksimalne pozicije od
  `250 mm`.
- Po zavrsetku inicijalizacije referentna/trenutna pozicija svakog aktuatora
  postavlja se na `250 mm`.
- Funkcija se koristi kada prikazane pozicije aktuatora nisu tacne.

### Attachment mod

- `Attachment` je on/off kontrola koja aktivira ili deaktivira implement lifting
  mode.
- Kada je mod aktivan, drzanje `Up` spusta prednje aktuatore i podize zadnje,
  cime se prikljucna masina podize od zemlje.
- Kada je mod aktivan, drzanje `Down` podize prednje aktuatore i spusta zadnje,
  cime se prikljucna masina spusta.
- Kada je mod neaktivan, `Up` i `Down` menjaju visinu celog robota u istom smeru
  na sva cetiri aktuatora.

Status/podaci:

- Trenutne pozicije aktuatora u mm.
- Rucno unete zeljene pozicije aktuatora.
- Da li je attachment mod aktivan.
- Da li je komanda u toku i da li je uspesno prihvacena.
- Status i rezultat inicijalizacije.

Napomene za novu aplikaciju:

- Ovo je sigurnosno osetljiv ekran. Mora jasno prikazati da li je komanda poslata,
  prihvacena, u toku, zavrsena ili neuspesna.
- Hold-to-run kontrole ne smeju ostati aktivne posle `pointer up`, `pointer
  cancel`, gubitka lifecycle fokusa ili veze.
- UI treba da bude pregledan na tabletu i da spreci slucajno aktiviranje preset,
  initialization i attachment komandi.
- Potrebno je proveriti tacno mapiranje dugmadi na postojece ROS servise/topice.
- Buduca blokada voznje prema visini aktuatora koristi iste podatke o poziciji,
  ali nije funkcija samog trenutnog Clearance ekrana.

Otvorena pitanja za proveru na postojecem sistemu:

- Dozvoljeni opseg i korak rucnog unosa, kao i ponasanje kada je neko polje
  prazno ili neispravno.
- Da li `Send position` uvek salje sva cetiri polja i kakvu potvrdu vraca robot.
- Tacan topic/service, payload i frekvencija slanja za `Up` i `Down` dok se drze.
- Da li robot ima poseban stop poziv za aktuatore ili se stop ostvaruje prekidom
  ponavljanja komande.
- Kako se prijavljuje delimican kvar ili zastoj samo jednog aktuatora.
- Da li se `Attachment` stanje cita sa robota i da li ostaje aktivno posle
  reconnect-a ili restarta.
- Da li `Initialization` zahteva dodatnu potvrdu i kako aplikacija prikazuje
  neuspeh kalibracije.

## Statistics placeholder iz starijeg APK-a

Stariji lokalni APK sadrzi vidljivu ikonu `Statistics`. Posle povezivanja sa
robotom ona se aktivira i otvara `StatisticsFragment`, ali taj fragment sadrzi
samo tekst `Statistics`. Nema grafike, telemetriju, ROS komunikaciju niti drugu
funkcionalnu logiku.

ADB provera aktivnog `com.coming.agarcontrol` paketa potvrdila je da Statistics
dugme nije vidljivo na MK32. Postojeci placeholder se zato ne smatra obaveznom
funkcijom nove aplikacije.

Ideja o statistici ostaje kandidat za novu aplikaciju, verovatno kroz live
telemetriju, dijagnostiku i istoriju rada, ali ne treba praviti placeholder dok
ne utvrdimo stvarne potrebe.

Ako u novoj aplikaciji bude potrebna prava statistika, treba definisati:

- Koji grafici ili tabele se prikazuju.
- Da li su podaci live ili iz logova.
- Da li prikazuje bateriju, PTO, motor rpm/current, misije, vreme rada ili greske.
- Da li postoji export/share funkcija.

## 5. Settings

Postojeci `Settings` je minimalan ekran za cuvanje parametara konekcije sa
robotom. Nije isto sto i brzi Settings podmeni na `Navigation` ekranu, koji
sadrzi slajdere voznje i `Brake`.

Sta se vidi:

- Polje za IP adresu robota.
- Polje za port.
- Dugme za cuvanje podesavanja.

Podrazumevane vrednosti:

- IP adresa: `192.168.0.61`.
- Port: `9090`.

Komande:

- Cuvanje upisane IP adrese i porta.
- Sacuvane vrednosti koriste se kada korisnik na pocetnom ekranu pritisne
  `Connect`.
- Samo povezivanje i prekid veze ostaju funkcije pocetnog ekrana, a ne ovog
  Settings ekrana.

Status/podaci:

- Trenutno sacuvana IP adresa.
- Trenutno sacuvan port.
- Potvrda da su podesavanja uspesno sacuvana ili poruka o gresci.

Napomene za novu aplikaciju:

- IP i port moraju se validirati pre cuvanja.
- Sacuvane vrednosti treba trajno cuvati lokalno i ponovo ucitati nakon
  restarta aplikacije.
- Ako nova aplikacija koristi robot gateway, Settings treba da podrzi gateway
  host/port.
- ROSBridge port `9090` moze ostati kao legacy/debug opcija.
- Za v1 gateway nije obavezan; aplikacija treba da radi direktno preko
  ROSBridge porta `9090`.
- Buzzer, actuator-position lock, SLAM modovi i ostale nove funkcije nisu deo
  postojeceg Settings ekrana. Njihovu poziciju u novoj aplikaciji odredicemo
  prilikom projektovanja nove informacione arhitekture.

Otvorene provere na postojecem sistemu:

- Gde i u kom formatu stara aplikacija trajno cuva IP adresu i port.
- Da li promena vrednosti utice samo na sledeci `Connect` ili prekida i ponovo
  uspostavlja vec aktivnu vezu.
- Koja validacija i poruke o gresci trenutno postoje.

## Nove korisne funkcije

- Global emergency stop vidljiv na glavnim ekranima.
- Health check robota.
- GPS valid/invalid indikator.
- Jetson/Pi/ROS/gateway online indikatori.
- PTO current/rpm trend.
- Kamera stream kvalitet: bitrate, fps, connection status.
- Fuzzy status: enabled/disabled, PTO derate faktor, razlog usporavanja.
- Buzzer mode/status.
- Zakljucavanje robota u odnosu na poziciju aktuatora.
- Buduci SLAM status: inactive/mapping/localizing/error.

Predlog konacnog grupisanja postojecih i novih funkcija, kao i ogranicenja
utvrdjena pregledom robot koda, nalaze se u
`docs/APP_INFORMATION_ARCHITECTURE.md`.
