<?php
require_once '../../libs/TokenManager.php';
require_once '../../libs/UserManager.php';
require_once '../../libs/PostManager.php';

if (!isset($_GET['token'])) {
    $json = ['status' => 'E00', 'msg' => ['REQUIRED_PARAM']];
} else {
    $user_id = TokenManager::get_user_id($_GET['token']);
    if (!$user_id) {
        $json = ['status' => 'E00', 'msg' => ['UNKNOWN_TOKEN']];
    } else {
        $profile = UserManager::get_profile($user_id);
        $post = PostManager::get_user_timeline($user_id);
        $json = ['status' => 'S00', 'data' => ['profile' => $profile, 'post' => $post]];
    }
}

header('Access-Control-Allow-Origin: *');
header("Content-Type: application/json; charset=utf-8");
echo json_encode($json, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);
