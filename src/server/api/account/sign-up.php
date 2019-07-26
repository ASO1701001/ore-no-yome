<?php
require_once '../../libs/AccountManager.php';

$error_msg = [];

if (!isset($_POST['user_id']) || !isset($_POST['user_name']) || !isset($_POST['email']) || !isset($_POST['password'])) {
    $json = ['status' => 'E00', 'msg' => ['REQUIRED_PARAM']];
} else {
    require_once '../../libs/ValidationManager.php';
    $validation_user_id = ValidationManager::fire($_POST['user_id'], ValidationManager::$USER_ID);
    $validation_user_name = ValidationManager::fire($_POST['user_name'], ValidationManager::$USER_NAME);
    $validation_email = ValidationManager::fire($_POST['email'], ValidationManager::$EMAIL);
    $validation_password = ValidationManager::fire($_POST['password'], ValidationManager::$PASSWORD);
    if (!$validation_user_id || !$validation_user_name || !$validation_email || !$validation_password) {
        if (!$validation_user_id) $error_msg[] = "VALIDATION_USER_ID";
        if (!$validation_user_name) $error_msg[] = "VALIDATION_USER_NAME";
        if (!$validation_email) $error_msg[] = "VALIDATION_EMAIL";
        if (!$validation_password) $error_msg[] = "VALIDATION_PASSWORD";
        $json = ['status' => 'E00', 'msg' => $error_msg];
    } else {
        $check_user_id = AccountManager::already_user_id($_POST['user_id']);
        $check_email = AccountManager::already_email($_POST['email']);
        if (!$check_user_id || !$check_email) {
            if (!$check_user_id) $error_msg[] = "ALREADY_USER_ID";
            if (!$check_email) $error_msg[] = "ALREADY_EMAIL";
            $json = ['status' => 'E00', 'msg' => $error_msg];
        } else {
            $user_id = AccountManager::sign_up(
                $_POST['user_id'],
                $_POST['user_name'],
                $_POST['email'],
                $_POST['password']
            );
            require_once '../../libs/TokenManager.php';
            $token = TokenManager::add_token($user_id);
            $json = ['status' => 'S00', 'data' => ['token' => $token]];
        }
    }
}

header('Access-Control-Allow-Origin: *');
header("Content-Type: application/json; charset=utf-8");
echo json_encode($json, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);