# building

Cephis requires Maven to build. It relies on a parent POM which should be used to edit resources uniformly (e.g. Java version, maven plugins, etc.).

## POM file
The POM file depends on a parent. 
```
    <parent>
        <groupId>org.contentmine</groupId>
        <artifactId>cm-parent</artifactId>
        <version>7.1.0</version>
    </parent>
```

The components of the POM include:

### jar-with-dependencies
The POM can generate a single Java JAR which contains all the upstream libraries so it is the only thing that has to be downloaded. There are deliberately many entry points

### appassembler
This generates platform-independent scripts (UNIX, MACOSX, Windows) which run the different functions. Currently this doesn't exist in `cephis` and most of these are in `norma` and `ami`. However if `cephis` has useful entry points they acn be added to the POM.
Current entry points:
```
contentMine          exist???
cproject             runs "cproject [args]
makeProject          runs - -makeProject (\\1)/fulltext.pdf - -fileFilter .*/(.*)\\.pdf"
norma                norma [args]
                            but also runs makeProject (FIX this)
ami-all              lists AMI commands (ami-*)
ami-dictionaries     edit/create AMI dictionaries
ami-search-cooccur   run AMI searches and co-occurrence
ami-pdf              convert PDF into SVG and extract images
ami-xml              ???
cmine                
ami-frequencies      
ami-gene             
ami-identifier       
ami-regex            
ami-search           
ami-sequence         
ami-species          
ami-word             
```
(This needs updating and will be).

## versioning
Currently all versions are `SNAPSHOTS` and treated a such by Maven. 


