@echo off                                                                                                    
                                                                                                             
if %1.==. (                                                                                                  
        echo No parameters have been provided. You need a json file as parameter                             
        echo Ex:  %0 [book's isbn]
) else (                                                                                                     
        echo "Run post json file to http://127.0.0.1:8888/bookstore/delete/%1"                                  
        curl.exe -H "Content-Type: application/json" -v -X DELETE -d @%1 http://127.0.0.1:8888/bookstore/delete/%1
)                                                                                                            
                                                                                                             
@echo on                                                                                                     