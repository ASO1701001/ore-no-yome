<?php
require_once '../../libs/TokenManager.php';

$error_msg = [];

if (!isset($_GET['token'])) {
    $json = ['status' => 'E00', 'msg' => ['REQUIRED_PARAM']];
} else {
    $verify = TokenManager::verify_token($_GET['token']);
    if ($verify) {
        $json = ['status' => 'S00'];
    } else {
        $json = ['status' => 'E00', 'msg' => ['UNKNOWN_TOKEN']];
    }
}

header('Access-Control-Allow-Origin: *');
header("Content-Type: application/json; charset=utf-8");
echo json_encode($json, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);