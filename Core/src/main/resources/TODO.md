# TODO

## Core
* Variant Selection : Hard-coded NFE --> remove or selectable population
* Variant Selection : for frequency f --> keep v <=f AND v >= 1-f
* Fisher Test on call rate, read from QC.param and sent to TPS as WSS parameter
* RPP creates the session, Client Asks the session AND THEN both start extracting ```DONE 1.0.0```
* QC during extraction ```DONE 1.0.1```
    
## Client
* Warning if extraction with criteria yields no data ```DONE 1.0.0```
* Status updated in log, if different from previous one ```DONE 1.0.0```
* Lock problem, GUI not closeable ```DONE 1.0.1```
* Log Icon for TPSLogWindow ```DONE 1.0.1```
* Add a menu to (re)open TPSLogWindow ```DONE 1.0.1```
* Results as Frame instead of JOptionPane ```DONE 1.0.1```
* Add a QC Parameter Editor ```DONE 1.0.1```
## RPP
* Broken config file
    - login **KO**
    - server **unreachable**
    - getKeyScript **missing**
    - getKeyScript **KO**
    - exec script **missing**
    - exec script **KO**    
* Warning if extraction with criteria yields no data ```DONE 1.0.0```
## TPS
* When a variant is missing in one set, all the genotypes are set to 0, it is also the case for partially missing genotypes. Should partially-missing genotypes be left to "missing" ? 
## Other
* Propose grcH38 datasets
