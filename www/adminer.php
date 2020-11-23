<?php
parse_str(implode('&', array_slice($argv, 1)), $_GET);
parse_str(implode('&', array_slice($argv, 1)), $_POST);
parse_str(implode('&', array_slice($argv, 1)), $_SERVER);
parse_str(implode('&', array_slice($argv, 1)), $_COKKIE);

if (!function_exists('getallheaders')) {
    function getallheaders() {
           $headers = [];
       foreach ($_SERVER as $name => $value) {
           if (substr($name, 0, 5) == 'HTTP_') {
               $headers[str_replace(' ', '-', ucwords(strtolower(str_replace('_', ' ', substr($name, 5)))))] = $value;
           }
       }
       return $headers;
    }
}

function endIt() {
	var_dump(headers_list());
	var_dump(getallheaders());
	var_dump($_COOKIE);
	/*var_dump($_GET);
	var_dump($_POST);
	var_dump($_SERVER);*/
	exit;
}
include 'adminer/adminer.php';