<?php
require_once '../../libs/PostManager.php';
$data = PostManager::get_chara_list_all();
$json = ['status' => 'S00', 'data' => $data];

header('Access-Control-Allow-Origin: *');
header("Content-Type: application/json; charset=utf-8");
echo json_encode($json, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);
