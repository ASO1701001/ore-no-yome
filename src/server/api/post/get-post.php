<?php
require_once '../../libs/PostManager.php';
require_once '../../libs/TokenManager.php';

if (!isset($_GET['post_id'], $_GET['token'])) {
    $json = ['status' => 'E00', 'msg' => ['REQUIRED_PARAM']];
} else {
    $user_id = TokenManager::get_user_id($_GET['token']);
    if (!$user_id) {
        $json = ['status' => 'E00', 'msg' => ['UNKNOWN_USER']];
    } else {
        $data = PostManager::get_post($_GET['post_id'], $user_id);
        if (is_null($data)) {
            $json = ['status' => 'E00', 'msg' => ['UNKNOWN_POST']];
        } else {
            $json = ['status' => 'S00', 'data' => $data];
        }
    }
}

header('Access-Control-Allow-Origin: *');
header("Content-Type: application/json; charset=utf-8");
echo json_encode($json, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);
