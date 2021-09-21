# Changelog

## 1.0.5 (2021-09-22)
### Core
### Client
### RPP
### TPS

## 1.0.4 (2021-09-21)
### Core
* GenotypesFileHandler : *added* `extractCanonicalAndHash` that allows getting hashed values from the original VCF file, in order to debug a session
* Use of external GnomAD exome/genome files (allows choosing GnomAD version)
* Classes `ExtractAnnotations`,`BinaryGnomadReader`,`BinaryGnomadWriter` and `GnomADIndexReader` to process a binary version of the GnomAD files
### Client
* ClientRun : *added* command line `--extractandhash`
* Filtering on external GnomAD files, with choice of subpopulation and choice of GnomAD version
* Better management of Exception 
### RPP
* RPPRun : *added* command line `--extractandhash`
* Filtering on external GnomAD files, with choice of subpopulation
### TPS

## 1.0.3 (2021-07-21)
### Core
* Typo : in javadoc
### Client
### RPP
### TPS
* WSS : *changed* references to **status** or **affected** are changed to *phenotypes*, **changed** `x()` methods to static 
* Shuffler : *changed* access to public for getNext()
* Utils : *added* class providing static methods that are independent of the algorithms
* TPSRun : *changed* output of `computeWSSRanksum`, add information and results of both optimized and unoptimized methods
* Gamma : *added* `toString()`, **changed** `add(boolean phenotype)` returns `true` instead of void
* RankedGammaList : *added* `add(int, boolean)`, `add(int, Gamma)`, `printDebug()`, **changed** `add(double, boolean)` simplified through use of boolean add instead of void

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
