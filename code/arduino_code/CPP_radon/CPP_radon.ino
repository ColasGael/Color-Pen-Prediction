
#include <CurieBLE.h> //library for BLE communication
// examples available here : https://www.arduino.cc/en/Reference/CurieBLE

BLEPeripheral blePeripheral; // BLE Peripheral Device (the board you're programming)
BLEService penService("a7b2fe81-25c5-4ca5-adfc-77e4f3834bea"); // BLE Pen Service

// BLE Parameters Characteristic
BLEByteCharacteristic parametersCharacteristic("a7b2fe81-25c5-4ca5-adfc-77e4f3834beb", BLERead | BLEWrite);

// BLE Current Weight Characteristic
BLEByteCharacteristic switchWeightCharacteristic("a7b2fe81-25c5-4ca5-adfc-77e4f3834bec", BLERead | BLENotify); 
// remote clients will be able to get notifications if this characteristic changes
// the characteristic is 2 bytes long as the first field needs to be "Flags" as per BLE specifications
// CAREFUL : the weight must not exceed 25.6g (because the date is stored in Hex on 2 characters : 16² = 256 = 25.6*10)

// BLE Current Pen Characteristic
BLEByteCharacteristic switchPenCharacteristic("a7b2fe81-25c5-4ca5-adfc-77e4f3834bed", BLERead | BLENotify); 
// remote clients will be able to get notifications if this characteristic changes
// https://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml

// useful hex-to-decimal converter : http://www.binaryhexconverter.com/hex-to-decimal-converter

#include <Hx711.h> //library for the weight sensor

Hx711 scale ; //the weight sensor
/* library design: Weihong Guan (@aguegu)
 * library host on : https://github.com/aguegu/ardulibs/tree/3cdb78f3727d9682f7fd22156604fc1e4edd75d1/hx711
 */

double mPot ;//the weight of the pot (in g)

const int max = 6 ; //the maximum number of pens that the sensor can detect
float mPen [max] ; //the array of the weights of the different pens (in g)

float mCurrentPen ; //the weight of the pen which has just been taken (in g)
int currentPen ; //the number of the pen which has just been taken

float mTot ;//the total weight measured by the sensor (in g)

int n = 0 ;//the number of pen used in the trick
float mEqual = 0.1 ; //the maximal difference in weight for which 2 different weight are considered equal (in g)
float mMin = 5 ;//the lower boundary of weight measured by the sensor (in g)

float delta = 0.5 ;//the minimal difference of weight allowed between 2 pens (in g)
int tMes = 50 ;//the time between 2 measurement of the sensor (in ms)
int nMes = 10 ;//the number of measurements taken to ensure that the measurement is stable

void setup() {
  Serial.begin(9600) ;
  // Hx711.DOUT - pin #A2
  // Hx711.SCK - pin #A3
  scale.begin(A2, A3) ;

  // set advertised local name and service UUID:
  blePeripheral.setLocalName("CPP");
  blePeripheral.setAdvertisedServiceUuid(penService.uuid());

  // add service and characteristic:
  blePeripheral.addAttribute(penService);
  // CAREFUL : you have to add the characteristics in the SAME order you defined them
  blePeripheral.addAttribute(parametersCharacteristic);
  blePeripheral.addAttribute(switchWeightCharacteristic);
  blePeripheral.addAttribute(switchPenCharacteristic);

  // set the initial value for the characteristics:
  parametersCharacteristic.setValue(0);
  switchWeightCharacteristic.setValue(0);
  switchPenCharacteristic.setValue(0);

  // begin advertising BLE service:
  blePeripheral.begin();
}

void loop() {

  // wait until a central is connected to peripheral:
  while(!blePeripheral.central()) {
      blePeripheral.central();
  }
  Serial.print("Connected to central: ");
  // print the central's MAC address:
  Serial.println(blePeripheral.central().address());

  // poll peripheral
  blePeripheral.poll();

  parameters() ;
  
  initialization();
  
  while (true){
    Serial.print("Le feutre qui vient d'être pris est le feutre numéro : ");
    Serial.println(takenPen());
    switchPenCharacteristic.setValue(currentPen);  // notify the user of the current pen which has been taken by the public
  }
}

boolean isWeightDifferent () {
  float difference [nMes] ;
  difference [0] = scale.getGram() - mTot ;
  difference [9] = difference[0] + 0.11 ;
  delay(tMes) ;

  int counter = 1 ;

  //compare nMes measurement taken at tMes interval to ensure that the measurement is stable 
  while ((maxAbsDiffVec(difference, nMes) > mEqual) || (abs(difference[0]) > 30)){
    difference[counter] = scale.getGram() - mTot ;
    delay(tMes);

    counter = (counter+1)%10 ;
  }

  //return "true" if the difference between the old and the new value of weight is superior to mMin : a pen has been taken/dropped
  float result = (abs(difference[0]) > mMin) ;
  return result ;
}

// get the set of parameters from the user
void parameters() {
  // wait until the parameter is received 
  while(parametersCharacteristic.value() == 0){
  }
  // the first parameter received is (int)50*mEqual
  mEqual = parametersCharacteristic.value()/50. ; //the maximal difference in weight for which 2 different weight are considered equal (in g)
  Serial.println(mEqual) ;

  // reset the characteristic to 0
  parametersCharacteristic.setValue(0) ;
  //notify the user that the parameter has been received
  switchPenCharacteristic.setValue(1) ;

  // wait until the parameter is received 
  while(parametersCharacteristic.value() == 0){
  }  
  // the second parameter received is (int)mMin
  mMin = parametersCharacteristic.value() ; //the lower boundary of weight measured by the sensor (in g)
  Serial.println(mMin) ;
 
  // reset the characteristic to 0
  parametersCharacteristic.setValue(0) ;
  //notify the user that the parameter has been received
  switchPenCharacteristic.setValue(2) ;
  
  // wait until the parameter is received 
  while(parametersCharacteristic.value() == 0){
  }  
  // the third parameter received is (int)10*delta
  delta = parametersCharacteristic.value()/10. ;//the minimal difference of weight allowed between 2 pens (in g)
  Serial.println(delta) ;

  // reset the characteristic to 0
  parametersCharacteristic.setValue(0) ;
  //notify the user that the parameter has been received
  switchPenCharacteristic.setValue(3) ;
  
  // wait until the parameter is received 
  while(parametersCharacteristic.value() == 0){
  }  
  // the forth parameter received is tMes/10
  tMes = 10*parametersCharacteristic.value() ;//the time between 2 measurement of the sensor (in ms)

  // reset the characteristic to 0
  parametersCharacteristic.setValue(0) ;
  //notify the user that the parameter has been received
  switchPenCharacteristic.setValue(4) ;
  Serial.println(tMes) ;
  
  // wait until the parameter is received 
  while(parametersCharacteristic.value() == 0){
  }  
  // the fifth parameter received is nMes
  nMes = parametersCharacteristic.value() ;//the number of measurements taken to ensure that the measurement is stable
  Serial.println(nMes) ;
  
  // reset the characteristic to 0
  parametersCharacteristic.setValue(0) ;
  //notify the user that the parameter has been received
  switchPenCharacteristic.setValue(5) ; 
}

// initialize the trick : register the number of pens, their weights
void initialization() {

  Serial.print("Combien de feutres voulez-vous mettre dans le pot ? \n") ;
  n = parametersCharacteristic.value() ; // nb of pens
  while (n == 0){// any value other than 0
    n = parametersCharacteristic.value() ; // nb of pens
  }
  Serial.print(n) ;
  Serial.print(" feutres vont être utilisés pour ce tour. \n") ;

  // reset the characteristic to 0
  switchPenCharacteristic.setValue(0) ;

  mTot = scale.getGram() ;

  //register the weight of the different pens in the mPen array
  for (int i=0 ; i<n ; i++) {
    Serial.print("Posez le feutre ") ;
    Serial.print(i+1) ;
    Serial.print(" sur la balance. \n") ;
    while (!isWeightDifferent()){
    }
    mPen[i] = scale.getGram() - mTot ;
    mTot = scale.getGram() ;
    Serial.print("Le feutre ") ;
    Serial.print(i+1) ;
    Serial.print(" a une masse de mFeutre = ") ;
    Serial.print(mPen[i], 1) ;
    Serial.print(" g. \n") ;

    // transmit mPen[i] rounded to the next 0.1 to the user
    float mPenRound = ((int)(mPen[i]*10)) ;
    switchWeightCharacteristic.setValue(mPenRound);  // notify the user of the weight of the pen which has just been added

    boolean alright = true ;
    
    for (int j = 0 ; j<i ; j++){
      if (abs(mPen[j] - mPen[i]) <= delta){
        Serial.print("Le feutre ") ;
        Serial.print(i+1) ;
        Serial.print(" a la même masse que le feutre ") ;
        Serial.print(j+1) ;
        Serial.print(".\n");
        Serial.print("Changez de feutre ") ;
        Serial.print(i+1) ;
        Serial.print(", puis repesez le.\n");

        // there is a problem
        alright = false ;
        
        while (mTot - scale.getGram() < mMin){ // wait until the user remove the pen
        }
        mTot = scale.getGram() ;
        i--;
        j = i ; //on sort de la boucle
      }
    }
    if (alright){
      // notify the user that the weight of the pen which has just been added is different from the weight of the other pens
      switchWeightCharacteristic.setValue(1); // the pen is fine
    }else{
      // notify the user that the weight of the pen which has just been added is similar to the weight of an other pen
      switchWeightCharacteristic.setValue(0); // the pen must be changed
    }
  }
} 

//return the number "currentPen" of the pen which has just been taken
int takenPen(){
    currentPen = 0 ;
  
    while (!isWeightDifferent()){
    }
    mCurrentPen = - scale.getGram() + mTot ;
    
    for (int i=0 ; i<n ; i++){
      if (abs(mCurrentPen - mPen[i]) <= delta/3){
        currentPen = i+1;
      }
    }
    mTot = scale.getGram() ;
    Serial.println(mCurrentPen) ;
    return currentPen ;
}

// compute the maximum of absolute values of the difference of 2 terms of vec
float maxAbsDiffVec (float vec [], int len){
  float max = abs(vec[0]- vec[1]) ;
  
  for (int i = 0; i < len; i++){
    for (int j = i+1; j < len; j++){
        if (abs (vec[i]-vec[j]) > max){
          max = abs (vec[i]-vec[j]) ;
        }
    }
  }
  return max ;
}


