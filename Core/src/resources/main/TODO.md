# TODO

## Core
* RPP creates the session, Client Asks the session AND THEN both start extracting ```DONE 1.0.0```
    - client still waits some time
* QC during extraction ```DONE 1.0.1```
    - client sends a qc extraction file (genotype file needs to be re-extracted from VCF)
    
## Client
* Warning if extraction with criteria yields no data ```DONE 1.0.0```
* Status updated in log, if different from previous one ```DONE 1.0.0```
* Lock problem, uncloseable Gui
* Log Icon for TPSLogWindow ```DONE 1.0.1```
* Add a menu to (re)open TPSLogWindow
* Results as Frame instead of JOptionPane ```DONE 1.0.1```
* Add a QC Parameter Editor
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
* Warning if RPP/Client data have no common genes (really ?)
## Other
* Propose grcH38 datasets
