library(stringi)
library(dplyr)
library(tidyverse)

#Zaczytanie
dfraw <- read.csv("cars_raw.csv")
#Wybór potrzebnych kolumn
sam <- dfraw %>%
  select(-Made.In, -Warranty, -Available.Colors,  -Charger,
         -Maximum.Speed, -Acceleration.Time..0.100.kmph., -Gear.Box, -Steering.Type, -Steering.Gear.Type, -Front.Suspension,
         -Back.Suspension,-Tyre.Size, -Wheel.Size, -Front.Brake, -Rear.Brake, -Minimum.Turning.Radius, -Tyre.Type, -Fuel.System,
         -Valve.Configuration, -Boot.Space, -Assembled.In, -Displacement..cc., -Shock.Absorbers.Type,
         -Introduction.Date)

#Obróbka Price
sam <- sam %>%
  mutate(Price = as.numeric(gsub("(,)", "", Price))) %>%
  drop_na(Price)

#Obróbka mocy
sam <- sam %>% 
  mutate(power_hp = as.numeric(stri_extract_first( gsub("(,)", "", Engine.Power) ,regex = "(\\d+)"))) %>%
  mutate(power_hp = ifelse(grepl("kw",stri_extract_first(Engine.Power,regex = "(bhp)|(hp)|(HP)|(kW)|(kw)|(KW)|(PS)"), ignore.case = TRUE),
                           round(power_hp * 1.34), power_hp)) %>%
  select(-Engine.Power)

#Pojemności
sam <- sam %>% 
  mutate(pojemnosc_L = as.numeric(stri_extract(Engine.Type ,regex = "([0-9])([.])([0-9])")))


#Moment obrotowy
sam <- sam %>% 
  mutate(moment_obrotowy_lb_ft = as.numeric(stri_extract_first( gsub("(,)", "", Torque) ,regex = "(\\d+)"))) %>%
  mutate(moment_obrotowy_lb_ft = ifelse(grepl("n",stri_extract_first(Torque,regex = "(Nm)|(nm)|(NM)|(l)|(L)|(pound)|(Newton)"), ignore.case = TRUE),
                                        round(moment_obrotowy_lb_ft * 0.74), moment_obrotowy_lb_ft)) %>%
  select(-Torque)

#Długość wyokość, szerokość, długość, prześwit, rozstaw osi
sam <- sam %>%
  mutate(wysokosc_in = as.numeric(stri_extract_first( gsub("(,)", "",Height ), regex = "(\\d+)[.](\\d+)|(\\d+)")),
         dlugosc_in = as.numeric(stri_extract_first( gsub("(,)", "",Length ), regex = "(\\d+)[.](\\d+)|(\\d+)")),
         szerokosc_in = as.numeric(stri_extract_first( gsub("(,)", "",Width ), regex = "(\\d+)[.](\\d+)|(\\d+)")),
         rozstaw_osi_in = as.numeric(stri_extract_first( gsub("(,)", "",Wheel.Base ), regex = "(\\d+)[.](\\d+)|(\\d+)")),
         przeswit_in = as.numeric(stri_extract_first( gsub("(,)", "",Ground.Clearance ), regex = "(\\d+)[.](\\d+)|(\\d+)"))) %>%
  mutate(wysokosc_in = ifelse(grepl("m",stri_extract_first(Height,regex = "(mm)|(MM)|(Mn)|(In)|(in)|(IN)"), ignore.case = TRUE),
                              round(wysokosc_in * 0.04), wysokosc_in),
         dlugosc_in = ifelse(grepl("m",stri_extract_first(Length,regex = "(mm)|(MM)|(Mn)|(In)|(in)|(IN)"), ignore.case = TRUE),
                             round(dlugosc_in * 0.04), dlugosc_in),
         szerokosc_in = ifelse(grepl("m",stri_extract_first(Width,regex = "(mm)|(MM)|(Mn)|(In)|(in)|(IN)"), ignore.case = TRUE),
                               round(szerokosc_in * 0.04), szerokosc_in),
         rozstaw_osi_in = ifelse(grepl("m",stri_extract_first(Wheel.Base,regex = "(mm)|(MM)|(Mn)|(In)|(in)|(IN)"), ignore.case = TRUE),
                                 round(rozstaw_osi_in * 0.04), rozstaw_osi_in),
         przeswit_in = ifelse(grepl("m",stri_extract_first(Ground.Clearance,regex = "(mm)|(MM)|(Mn)|(In)|(in)|(IN)"), ignore.case = TRUE),
                              round(przeswit_in * 0.04), przeswit_in)) %>%
  select(-c(Length, Height, Width, Wheel.Base, Ground.Clearance))

#Cylindry
sam <- sam %>%
  mutate(cylindry = as.numeric(stri_extract_first( No..of.Cylinders,regex = "(\\d+)" ))) %>%
  select(-No..of.Cylinders)



#Pojemnosc baku
sam <- sam %>%
  mutate(bak = gsub("[^a-zA-Z]", "", Fuel.Tank.Capacity..Litres.)) %>%
  mutate(bak = case_when(
    bak %in% c("Liters", "L", "liters", "Litres", "litres", "Liter",
               "LITRES", "l", "", "lgal") ~ round(as.numeric(stri_extract(Fuel.Tank.Capacity..Litres., regex="\\d+(?=\\D|\\s)"))),
    bak %in% c("gallons", "gal", "Gallon", "LiterGallon",
               "Gallons", "gallon") ~ round(as.numeric(stri_extract(Fuel.Tank.Capacity..Litres., regex="\\d+(?=\\D|\\s)")) * 3.785),
    is.na(bak) ~ 0,
    TRUE ~ 0)) %>%
  select(-Fuel.Tank.Capacity..Litres.)


#Skrzynia Biegow
sam <- sam %>%
  mutate(gear = ifelse(stri_extract(Transmission.Type,regex = "(Continuously)|(CVT)|(continuously)") 
                       %in% c("Continuously", "CVT", "continuously"),
                       "CVT", Transmission.Type)) %>%
  mutate(gear = ifelse(stri_extract(Transmission.Type,regex = "(Manual)|(manual)") 
                       %in% c("Manual", "manual"), "Manual", gear)) %>%
  mutate(skrzynia_biegow = ifelse(gear %in% c("Manual", "CVT"), gear, "Automatic")) %>%
  select(-Transmission.Type, -gear)

#Naped
sam <- sam %>%
  mutate(naped = stri_extract_first(Drive.Type , regex = "(2)|(Front)|(FWD)|(Rear)|(Real)|(RWD)|(All)|(Al)|(AWD)|(4)|(Four)")) %>%
  mutate(naped = case_when(naped == "2" | naped == "Front" |naped == "FWD" ~ "front",
                           naped == "Rear" | naped == "Real" | naped == "RWD"  ~ "rear",
                           naped == "All" | naped == "Al" | naped == "AWD"| naped == "4"| naped == "Four"  ~  "all",
                           TRUE ~ "")) %>%
  select(-Drive.Type)

#siedzenia, drzwi
sam <- sam %>%
  mutate(siedzenia = as.numeric(stri_extract_first(Seating.Capacity , regex = "(\\d+)")),
         drzwi = as.numeric(stri_extract_first(No..of.Doors , regex = "(\\d+)"))) %>%
  select(-c(No..of.Doors, Seating.Capacity))

#waga
#chyba do popraway
sam <- sam %>%
  mutate(waga_lbs = as.numeric(stri_extract_first( gsub("(,)", "",Kerb.Weight ), regex = "(\\d+)[.](\\d+)|(\\d+)"))) %>%
  mutate(waga_lbs = ifelse(grepl("k",stri_extract_first(Kerb.Weight,regex = "(l)|(L)|(K)|(k)"), ignore.case = TRUE),
                           round(waga_lbs * 2.2), waga_lbs)) %>%
  select(-Kerb.Weight)


# Paliwo
sam <- sam %>%
  mutate(paliwo = case_when(Fuel.Type %in% c("Gasoline", "Premium", "Petrol", "Premium Unleaded", "Premium unleaded",
                                             "Regular", "Unleaded Premium", "Regular, Premium", "Gas", "Premium unleaded gasoline",
                                             "Premium, Regular", "Pertol", "Regular unleaded", "CNG") ~ "Petrol",
                            Fuel.Type %in% c("Electric", "Electricity", "Electric Fuel") ~ "Electric",
                            Fuel.Type %in% c("Hybrid", "Plug-in Hybrid", "Plug-In Electric/Gas",
                                             "Gas/Hybrid", "Plug-In Hybrid") ~ "Hybrid",
                            Fuel.Type %in% c("Diesel", "diesel") ~ "Diesel",
                            Engine.Type == "Electric" ~ "Electric",
                            TRUE ~ "Unknown"))

#spalanie
sam <- sam %>%
  mutate(city_mpg = gsub("[^a-zA-Z]", "", Mileage.in.City)) %>%
  mutate(city_mpg = case_when(city_mpg %in% c("LKm","Lkm","lKm","lkm") ~ 
                                round(235 / as.numeric(stri_extract_first(Mileage.in.City , regex = "(\\d+)[.](\\d+)|(\\d+)"))),
                              grepl("k",stri_extract_first(city_mpg , regex = "(K)|(k)"),ignore.case = TRUE) ~ 
                                round(as.numeric(stri_extract_first(Mileage.in.City , regex = "(\\d+)[.](\\d+)|(\\d+)")) * 2.35),
                              TRUE ~ round(as.numeric(stri_extract_first(Mileage.in.City , regex = "(\\d+)[.](\\d+)|(\\d+)"))))) %>%
  mutate(city_mpg = ifelse(paliwo == "Electric", 0, city_mpg)) %>%
  mutate(city_mpg = ifelse(city_mpg > 70, 0, city_mpg))

sam <- sam %>%
  mutate(highway_mpg = gsub("[^a-zA-Z]", "", Mileage.on.Highway)) %>%
  mutate(highway_mpg = case_when(highway_mpg %in% c("LKm","Lkm","lKm","lkm") ~ 
                                   round(235 / as.numeric(stri_extract_first(Mileage.on.Highway , regex = "(\\d+)[.](\\d+)|(\\d+)"))),
                                 grepl("k",stri_extract_first(highway_mpg , regex = "(K)|(k)"),ignore.case = TRUE) ~ 
                                   round(as.numeric(stri_extract_first(Mileage.on.Highway , regex = "(\\d+)[.](\\d+)|(\\d+)")) * 2.35),
                                 TRUE ~ round(as.numeric(stri_extract_first(Mileage.on.Highway , regex = "(\\d+)[.](\\d+)|(\\d+)"))))) %>%
  mutate(highway_mpg = ifelse(paliwo == "Electric", 0, highway_mpg)) %>%
  mutate(highway_mpg = ifelse(highway_mpg > 70, 0, highway_mpg)) %>%
  select(-c( Mileage.in.City, Mileage.on.Highway))


sam <- sam %>% 
  mutate(nadwozie = case_when(Body.Type %in% c("Sport Utility", "Crossover", "SUV    Category: Exotic SUV",
                                               "Mini-SUV", "Body Code: S , Body Style: Sport Utility",
                                               "crossover", "Luxury midsize SUV", "Mid-size luxury crossover 4-door SUV",
                                               "Midsize SUV",  "MUV") ~ "SUV",
                              Body.Type %in% c("Sedan   Category: Exotic", "4-door sedan luxury car",
                                               "luxury sedan", "Sedan Body") ~ "Sedan",
                              Body.Type %in% c("Sports Car", "Roadster", "Coupe   Category: Exotic",
                                               "Coupe   Category: Sports Car", "Coupe, Hatchback   Category: Sports Car",
                                               "Coupe Convertible", "Roadster   Category: Exotic",
                                               "Spider", "Sport Car", "Sports","Spyder", "Luxury coupe",
                                               "Roadster   Category: Sports Car") ~ "Coupe",
                              Body.Type %in% c("Hatchback   Category: City Car   Assembly: Toluca, MX",
                                               "5-door hatchback", "Hatchback", "Hatchback   Category: City Car",
                                               "Hatchback   Category: Sport car", "Mini","‎Hatchback", 
                                               "Hatchback   Category: Sports Car") ~ "Hatchback",
                              Body.Type %in% c("Soft Top Convertible", "Hard Top Convertible",
                                               "Convertible   Category: City Car, Compact sports car, Convertible",
                                               "Convertible   Category: Sports Car   Assembly: Toluca, MX") ~ "Convertible",
                              Body.Type %in% c("Heavy Duty Truck", "Pickup Truck", "Electric Truck") ~ "Truck",
                              Body.Type %in% c("Minivan", "Passenger Van") ~ "Van",
                              Body.Type %in% c("Wagen") ~ "Wagon",
                              Body.Type %in% c("None", "99+") ~ "",
                              TRUE ~ Body.Type)) %>%
  select(-Body.Type)

#Model
gsub_new<-function(model ,brand){
  return (gsub(brand, "", model))
}
nowy_model <- rep(0, nrow(sam))

for (row in 1:nrow(sam)) {
  nowy_model[row] = gsub_new(sam$Model.Number[row],sam$Brand[row])
}
sam <- sam %>%
  mutate(Model.Number = nowy_model)

dfraw<- sam
#Dodawanie cech
dfraw <- dfraw %>% 
  mutate(city_mpg = ifelse(Fuel.Type == "Electric", 70, city_mpg))

dfraw <- dfraw %>%
  mutate(bak = ifelse(bak > 200, 100, bak))
# Wyposażenie
# Patrzę ile jest dodatkowego wyposażenia, normuje (od 15 bo większość ma tego wyposażenie 30-40, żeby były większe różnice)
# tak, żeby punkty były od 0 do 10
dfraw <- dfraw %>%
  mutate(Safety = rowSums(dfraw[, 7:59] == "Yes")) %>%
  mutate(Safety = ifelse(Safety < 15, 0, round((Safety - 15) / (max(Safety) - 15) * 10,1)))

# Spalanie miasto, trasa, średnie
dfraw <- dfraw %>% 
  mutate(Punkty_spalanie_miasto = ifelse(is.na(city_mpg), 
                                         0, 
                                         round((city_mpg - min(city_mpg, na.rm = T)) / (max(city_mpg, na.rm = T) - min(city_mpg, na.rm = T)) * 10,1))) %>% 
  mutate(Punkty_spalanie_trasa = ifelse(is.na(highway_mpg), 
                                        0, 
                                        round((highway_mpg - min(highway_mpg, na.rm = T)) / (max(highway_mpg, na.rm = T) - min(highway_mpg, na.rm = T)) * 10,1))) %>% 
  mutate(Punkty_spalanie_srednie = round((Punkty_spalanie_miasto + Punkty_spalanie_trasa) / 2,1))



# Moc/masa
dfraw <- dfraw %>% 
  mutate(moc_masa = ifelse(is.na(waga_lbs),
                           ifelse(is.na(power_hp), 100 / 6000, power_hp / 6000),
                           ifelse(is.na(power_hp), 100 / waga_lbs, power_hp / waga_lbs)))

# Dynamika 
dfraw <- dfraw %>%
  mutate(Punkty_moc_masa = ifelse(moc_masa > 0.15, 10,
                                  round((moc_masa - min(moc_masa)) / (0.15 - min(moc_masa)) * 10,1))) %>%
  mutate(Punkty_moment_obrotowy = ifelse(is.na(moment_obrotowy_lb_ft), 
                                         0,
                                         ifelse( moment_obrotowy_lb_ft >= 400,
                                                 10,
                                                 round((moment_obrotowy_lb_ft - min(moment_obrotowy_lb_ft, na.rm = T)) / (400 - min(moment_obrotowy_lb_ft, na.rm = T)) * 10,1)))) %>% 
  mutate(Punkty_dynamika = round((Punkty_moc_masa + Punkty_moment_obrotowy) / 2, 1)) %>% 
  mutate(Punkty_dynamika = round((Punkty_dynamika - min(Punkty_dynamika)) / (max(Punkty_dynamika) - min(Punkty_dynamika)) * 10,1))




# Właściwości terenowe
dfraw <- dfraw %>% 
  mutate(Punkty_naped_terenowy = case_when(naped == "rear" ~ 10,
                                           naped == "all" ~ 7,
                                           naped == "front" ~ 3,
                                           T ~ 0)) %>% 
  mutate(Punkty_nadwozie_terenowe = case_when(nadwozie == "Truck" ~ 10,
                                              nadwozie == "SUV" ~ 8,
                                              nadwozie == "Hatchback" ~ 6.5,
                                              nadwozie == "Van" ~ 5,
                                              nadwozie %in% c("Wagon", "Sedan") ~ 3,
                                              nadwozie == "Coupe" ~ 2,
                                              nadwozie == "Convertible" ~ 1,
                                              T ~ 0)) %>% 
  mutate(Punkty_przeswit = case_when(is.na(przeswit_in) ~ 0,
                                     przeswit_in > 20 ~ 10,
                                     T ~ round((przeswit_in - min(przeswit_in, na.rm = T)) / (16 - min(przeswit_in, na.rm = T)) * 10,1))) %>% 
  mutate(Punkty_wlasciwosci_terenowe = 
           (Punkty_naped_terenowy + Punkty_nadwozie_terenowe + Punkty_przeswit)/3) %>% 
  mutate(Punkty_wlasciwosci_terenowe = 
           round((Punkty_wlasciwosci_terenowe - min(Punkty_wlasciwosci_terenowe)) / (max(Punkty_wlasciwosci_terenowe) - min(Punkty_wlasciwosci_terenowe)) * 10, 1))


# Bezpieczeństwo
dfraw <- dfraw %>%
  mutate(Punkty_bezpieczenstwo_wyposazenie = rowSums(dfraw[, c("AntiLock.Braking.System",
                                                               "Power.Steering",
                                                               "Anti.Lock.Braking",
                                                               "Brake.Assist",
                                                               "Central.Locking.1", 
                                                               "Night.Rear.View.Mirror",
                                                               "Rear.Seat.Belts",
                                                               "Door.Ajar.Warning",
                                                               "Crash.Sensor", 
                                                               "Engine.Check.Warning",
                                                               "Rear.Camera",
                                                               "Electric.Folding.Rear.View.Mirror",
                                                               "Automatic.Climate.Control",
                                                               "Parking.Sensors",
                                                               "Seat.Belt.Warning",
                                                               "Vehicle.Stability.Control.System",
                                                               "Engine.Immobilizer",
                                                               "Fog.Lights.Front...Back", 
                                                               "Outside.Temperature.Display",
                                                               "Tyre.Pressure.Monitor")] == "Yes")) %>% 
  mutate(Punkty_bezpieczenstwo_wyposazenie = 
           round((Punkty_bezpieczenstwo_wyposazenie - 2)/(20-2) * 10, 1)) %>% 
  mutate(rozmiar = wysokosc_in * dlugosc_in * szerokosc_in) %>%
  mutate(Punkty_rozmiar = case_when(is.na(rozmiar) ~ 0,
                                    rozmiar >= 2000000 ~ 10,
                                    T ~ round((rozmiar - min(rozmiar, na.rm = T)) / (2000000 - min(rozmiar, na.rm = T)) * 10, 1))) %>% 
  mutate(Punkty_bezpieczenstwo = round((Punkty_rozmiar + Punkty_bezpieczenstwo_wyposazenie) / 2,1)) %>% 
  mutate(Punkty_bezpieczenstwo = round((Punkty_bezpieczenstwo - min(Punkty_bezpieczenstwo)) / (max(Punkty_bezpieczenstwo) - min(Punkty_bezpieczenstwo)) * 10,1))



# Sportowy charakter

dfraw <- dfraw %>% 
  mutate(Punkty_rozmiar_silnika = case_when(is.na(pojemnosc_L) ~ 0,
                                            T ~ round((pojemnosc_L - min(pojemnosc_L, na.rm = T)) / (max(pojemnosc_L, na.rm = T) - min(pojemnosc_L, na.rm = T)) * 10, 1))) %>% 
  mutate(Punkty_naped_sportowy = case_when(naped == "rear" ~ 10,
                                           naped == "all" ~ 8,
                                           naped == "front" ~ 2,
                                           T ~ 0)) %>% 
  mutate(Punkty_cylindry = case_when(is.na(cylindry) ~ 0,
                                     cylindry > 8 ~ 10,
                                     T ~ round((cylindry - min(cylindry, na.rm = T)) / (8 - min(cylindry, na.rm = T)) * 10, 1))) %>% 
  mutate(Punkty_nadwozie_sportowe = case_when(nadwozie %in% c("Coupe", "Convertible") ~ 10,
                                              nadwozie == "Hatchback" ~ 8,
                                              nadwozie == "Sedan" ~ 6,
                                              nadwozie == "SUV" ~ 4,
                                              nadwozie %in% c("Wagon", "Van") ~ 2,
                                              nadwozie == "Truck" ~ 1,
                                              T ~ 0)) %>% 
  mutate(Punkty_sportowy_charakter = round((Punkty_rozmiar_silnika + Punkty_naped_sportowy + Punkty_cylindry + Punkty_nadwozie_sportowe + Punkty_moc_masa) / 5 , 1)) %>% 
  mutate(Punkty_sportowy_charakter = 
           round((Punkty_sportowy_charakter - min(Punkty_sportowy_charakter)) / (max(Punkty_sportowy_charakter) - min(Punkty_sportowy_charakter)) * 10, 1))



# Cena 
dfraw <- dfraw %>% 
  mutate(Punkty_cena = case_when(is.na(Price) ~ 0,
                                 Price > 200000 ~ 0,
                                 T ~ 10 - round((Price - min(Price, na.rm = T)) / (200000 - min(Price, na.rm = T)) * 10, 1)))



# Przeznaczenie miasto 
dfraw <- dfraw %>% 
  mutate(Punkty_rozmiar_miasto = case_when(is.na(rozmiar) ~ 0,
                                           rozmiar >= 2000000 ~ 0,
                                           T ~ 10 - round((rozmiar - min(rozmiar, na.rm = T)) / (2000000 - min(rozmiar, na.rm = T)) * 10, 1))) %>% 
  mutate(Punkty_nadwozie_miasto = case_when(nadwozie %in% c("Coupe", "Convertible") ~ 6,
                                            nadwozie == "Hatchback" ~ 10,
                                            nadwozie == "Van" ~ 2,
                                            nadwozie == "SUV" ~ 4,
                                            nadwozie %in% c("Wagon", "Sedan") ~ 6,
                                            nadwozie == "Truck" ~ 1,
                                            T ~ 0)) %>% 
  mutate(Punkty_miasto = round((Punkty_rozmiar_miasto + Punkty_spalanie_miasto + Punkty_nadwozie_miasto) / 3, 1)) %>% 
  mutate(Punkty_miasto = round((Punkty_miasto - min(Punkty_miasto)) / (max(Punkty_miasto) - min(Punkty_miasto)) * 10 , 1))

# Przeznaczenie trasy
dfraw <- dfraw %>% 
  mutate(Punkty_paliwo_trasa = case_when(paliwo %in% c("Petrol", "Diesel", "Hybrid") ~ 10,
                                         paliwo == "Electric" ~ 2, 
                                         T ~ 0)) %>% 
  mutate(Punkty_pojemnosc_baku = case_when(is.na(bak) ~ 0,
                                           bak >= 70 ~ 10,
                                           T ~ round((bak - min(bak, na.rm = T)) / (70 - min(bak, na.rm = T)) * 10, 1))) %>%
  mutate(Punkty_nadwozie_trasa = case_when(nadwozie %in% c("Coupe", "Convertible") ~ 7,
                                           nadwozie == "Hatchback" ~ 3,
                                           nadwozie == "Van" ~ 8,
                                           nadwozie == "SUV" ~ 5,
                                           nadwozie %in% c("Wagon", "Sedan") ~ 10,
                                           nadwozie == "Truck" ~ 1,
                                           T ~ 0)) %>%   
  mutate(Punkty_trasa = round((Punkty_paliwo_trasa +  Punkty_spalanie_trasa + Punkty_nadwozie_trasa + Punkty_pojemnosc_baku) / 5, 1)) %>% 
  mutate(Punkty_trasa = round((Punkty_trasa - min(Punkty_trasa)) / (max(Punkty_trasa) - min(Punkty_trasa)) * 10, 1))


# Przeznaczenie rodzinny
dfraw <- dfraw %>% 
  mutate(Punkty_siedzenia = case_when(is.na(siedzenia) ~ 0,
                                      T ~ round((siedzenia - min(siedzenia, na.rm = T)) / (max(siedzenia, na.rm = T) - min(siedzenia, na.rm = T)) * 10, 1))) %>% 
  mutate(Punkty_nadwozie_rodzinny = case_when(nadwozie %in% c("Coupe", "Convertible") ~ 0,
                                              nadwozie == "Hatchback" ~ 2,
                                              nadwozie == "Van" ~ 10,
                                              nadwozie == "SUV" ~ 8,
                                              nadwozie %in% c("Wagon", "Sedan") ~ 7,
                                              nadwozie == "Truck" ~ 4,
                                              T ~ 0)) %>% 
  mutate(Punkty_rodzinny = round((Punkty_siedzenia + Punkty_rozmiar + Punkty_nadwozie_rodzinny) / 3, 1)) %>% 
  
  mutate(Punkty_rodzinny = round((Punkty_rodzinny - min(Punkty_rodzinny)) / (max(Punkty_rodzinny) - min(Punkty_rodzinny)) * 10, 1))


# Przeznaczenie uniwersalny
dfraw <- dfraw %>% 
  mutate(Punkty_uniwersalny = round((Punkty_miasto + Punkty_rodzinny + Punkty_trasa) / 3, 1)) %>% 
  mutate(Punkty_uniwersalny = round((Punkty_uniwersalny - min(Punkty_uniwersalny)) / (max(Punkty_uniwersalny) - min(Punkty_uniwersalny)) * 10, 1))

#Rok
dfraw <- dfraw %>%
  mutate(flag = case_when(grepl(c("2019"),Model.Number)~ T,
                          grepl(c("2020"),Model.Number)~ T,
                          grepl(c("2021"),Model.Number)~ T,
                          TRUE ~ F)) %>%
  filter(flag==F) %>%
  select(-flag)
 

##
df <- dfraw %>%
  select(Brand, Price, Photo, Model.Number, Engine.Type, power_hp,
         moment_obrotowy_lb_ft, skrzynia_biegow, naped, siedzenia,
         drzwi, waga_lbs,dlugosc_in,szerokosc_in,wysokosc_in, paliwo, city_mpg, highway_mpg, nadwozie, Safety,
         Punkty_dynamika, Punkty_wlasciwosci_terenowe, Punkty_bezpieczenstwo,
         Punkty_sportowy_charakter, Punkty_cena, Punkty_miasto, Punkty_trasa,
         Punkty_rodzinny, Punkty_uniwersalny, Punkty_spalanie_srednie)

colnames(df) <- c("Brand","Pricing","Photo","Model","Engine","Power",
                  "Torque_lb_ft","Gearbox","Drivetrain","Seats",
                  "Doors","Weight_lbs","Length","Width","Height","Fuel","Consumption_city",
                  "Consumption_highway","Body","Equipment",
                  "Dynamics","Off-road capabilities",
                  "Safety","Sport character",
                  "Price","Punkty_miasto",
                  "Punkty_trasa","Punkty_rodzinny",
                  "Punkty_uniwersalny","Efficiency")

df <- df %>%
  mutate(Result = 0)

write.csv(df, "cars.csv", row.names=FALSE)