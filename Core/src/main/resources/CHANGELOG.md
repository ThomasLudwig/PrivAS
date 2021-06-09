# Changelog

## 1.0.2 (2021-06-09)
### Core
### Client
### RPP
* RPP : *added* blacklist/whitelist of clients and connection log to prevent too frequent connections
### TPS
* WSS : *fixed* Count of *shared* variants within genes
* WSS : *optimized* `x()` method, taking into account that there will be no missing data
* WSS : *changed* externalisation of classes `Gamma` and `RankedGammaList`
* ThirdPartyServer : *fixed* use of static final variable instead of hard-coded values

## 1.0.1 (2020-12-03)
### Core
* Quality Control : *added* QC on VCF data. The same QC is performed both on the Client's and RPP's data.
### Client
* TPSStatus : *added* TPSLogWindow
* ResultsPane : *changed* JFrame instead of JOptionPane
### RPP
### TPS

## 1.0.0 (2020-10-02)
### Core
* Message : **added** class StartSession and SessionStarted to delay extraction
### Client
* End of Association Tests : **added** automatic results download
### RPP
* Session creation : **changed** to delay extraction
* Data Extraction : **added** warning if extraction yields empty dataset
### TPS
* TPS Start : **added** number of genes/variants in initial Status
