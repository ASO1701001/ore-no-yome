<?php
require_once '../../libs/AccountManager.php';

$error_msg = [];

if (!isset($_POST['user_id']) || !isset($_POST['password'])) {
    $json = ['status' => 'E00', 'msg' => ['REQUIRED_PARAM']];
} else {
    $user_id = AccountManager::sign_in($_POST['user_id'], $_POST['password']);
    if (!$user_id) {
        $json = ['status' => 'E00', 'msg' => ['UNKNOWN_USER']];
    } else {
        require_once '../../libs/TokenManager.php';
        $token = TokenManager::add_token($user_id);
        $json = ['status' => 'S00', 'data' => ['token' => $token]];
    }
}

header('Access-Control-Allow-Origin: *');
header("Content-Type: application/json; charset=utf-8");
echo json_encode($json, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);