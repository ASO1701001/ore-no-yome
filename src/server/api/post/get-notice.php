<?php
require_once '../../libs/TokenManager.php';

$error_msg = [];

if (!isset($_GET['token'])) {
    $json = ['status' => 'E00', 'msg' => ['REQUIRED_PARAM']];
} else {
    $user_id = TokenManager::get_user_id($_GET['token']);
    if (!$user_id) {
        $json = ['status' => 'E00', 'msg' => ['UNKNOWN_TOKEN']];
    } else {

    }
}

header('Access-Control-Allow-Origin: *');
header("Content-Type: application/json; charset=utf-8");
echo json_encode($json, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);
