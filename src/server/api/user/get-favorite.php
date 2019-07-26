<?php
require_once '../../libs/UserManager.php';

if (!isset($_GET['user_id'])) {
    $json = ['status' => 'E00', 'msg' => 'REQUIRED_PARAM'];
} else {
    $user_id = UserManager::get_user_number_id($_GET['user_id']);
    if (!$user_id) {
        $json = ['status' => 'E00', 'msg' => 'UNKNOWN_USER'];
    } else {
        $data = UserManager::get_favorite($user_id);
        $json = ['status' => 'S00', 'data' => $data];
    }
}

header('Access-Control-Allow-Origin: *');
header("Content-Type: application/json; charset=utf-8");
echo json_encode($json, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);
