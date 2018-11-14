# CProject

The `CProject` is the key data structure in `norma` `ami` and `getpapers`. It's a directory, containing subdirectories (`CTree`s) with a large number of 
optional sub-sub-directories and sub-files, generally with reserved names or syntax. All input, temporary, and output files are stored in the `CProject`
system. This means they are:

* findable by humans (e.g. with a file browser)
* findable by machines (e.g. search tools on commandlines: `grep`, `find`)
* transformable by commonly available tools (`sed`, `emacs`, and user programs)
* aggregatable or importable into databases

## structure
an example: (comments as //...) Note the plastic nature of the tree, with everything optional
the order of files is unimportant
```
bmc                        // the CProject ("bmc")
├── 15_1_511               // first CTree ("15_1_511")
│   ├── expected           // reserved name, file created by user
│   │   └── regex          // reserved name; file created by user
│   │       └── consort0   // user name
│   │           └── results.xml  // reserved name
│   ├── fulltext.html            // reserved name (downloaded by getpapers)
│   ├── fulltext.pdf             // reserved name (downloaded by getpapers)
│   ├── fulltext.xml             // reserved name (downloaded by getpapers)
│   ├── quickscrape_result.json  // reserved name (from quickscrape)
│   └── results.json       // reserved name (from getpapers)
├── http_www.trialsjournal.com_content_16_1_1   // first CTree ("15_1_511")
│   ├── 1745-6215-16-1-1.gif   // user name 
│   ├── expected
│   │   ├── identifier
│   │   │   ├── clin.isrctn
│   │   │   │   └── results.xml
│   │   │   └── clin.nct
│   │   │       └── results.xml
│   │   └── word
│   │       ├── frequencies
│   │       │   ├── results.html
│   │       │   └── results.xml
│   │       └── lengths
│   │           ├── results.html
│   │           └── results.xml
│   ├── fulltext.html
│   ├── fulltext.pdf
│   ├── fulltext.xml
│   └── results.json
```

## filenames
When filenames are not reserved (e.g. `cProject` and `cTree`s it is STRONGLY recommended to use only alphanumeric characters and "-_.". Spaces and other punctuation cause serious problems when importing and exporting. Although we support UNICODE many other systems do not and so we recommend restricting to `[A-Za-z0-9._-]` . Some of our tools may elide spaces and change other punctuation to "_"

## transformations
The philosophy is to bring the tool to the data (CProject), transform it 
