# GnomAD Binary Files

## File Format

In order to perform reliable Association Tests, it is crucial that the frequency threshold considered are the same of all dataset.

To ensure that this is true, PrivAS annotates variants why frequencies from GnomAD ([https://gnomad.broadinstitute.org/](https://gnomad.broadinstitute.org/)).

GnomAD data are distributed as VCF File that can be very large. To prevent the RPP and Client from having to download those files, we have converted them into an in-house binary format, that only contains the informations necessary tp PrivAS.

It is very important, during an Association Test session that all parties use the same GnomAD Version to annotate their variants as frequency is one of the major selection criteria for the variants. 

## Download

| Version of GnomAD | Binary File | Required Index File |
|-------------------|-------------|---------------------|
| r2.1.1 | [GnomAD.r2.1.1.bin](https://lysine.univ-brest.fr/privas/gnomad/GnomAD.r2.1.1.bin) | [GnomAD.r2.1.1.bin.idx](https://lysine.univ-brest.fr/privas/gnomad/GnomAD.r2.1.1.bin.idx) |
| r2.0.1 | [GnomAD.r2.0.1.bin](https://lysine.univ-brest.fr/privas/gnomad/GnomAD.r2.0.1.bin) | [GnomAD.r2.0.1.bin.idx](https://lysine.univ-brest.fr/privas/gnomad/GnomAD.r2.0.1.bin.idx) |
