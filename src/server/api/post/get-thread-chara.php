<?php
require_once '../../libs/TokenManager.php';

$error_msg = [];

if (!isset($_GET['chara_id'])) {
    $json = ['status' => 'E00', 'msg' => ['REQUIRED_PARAM']];
} else {
    require_once '../../libs/PostManager.php';
    $data = PostManager::get_thread_chara($_GET['chara_id']);
    $json = ['status' => 'S00', 'data' => $data];
}

header('Access-Control-Allow-Origin: *');
header("Content-Type: application/json; charset=utf-8");
echo json_encode($json, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);
