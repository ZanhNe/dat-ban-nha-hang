import { useState } from 'react';
import adminService from '../../services/adminService';

const useBroadcastCenter = () => {
    const [isSending, setIsSending] = useState(false);

    const handleSendNotification = async (formData) => {
        setIsSending(true);
        try {
            let res;


            if (formData.targetType === 'INDIVIDUAL') {

                if (!formData.targetUserId) {
                    alert("Vui lòng nhập ID người dùng!");
                    return false;
                }
                res = await adminService.sendPersonalNotification({
                    userId: parseInt(formData.targetUserId),
                    title: formData.title,
                    content: formData.content,
                    type: formData.type
                });
            } else {

                const payload = {
                    title: formData.title,
                    content: formData.content,
                    type: formData.type
                };
                if (formData.targetType === 'ROLE') {
                    payload.targetRole = formData.targetRole;
                }
                res = await adminService.sendBroadcastNotification(payload);
            }

            if (res.status === 200) {
                alert(" " + res.message);
                return true;
            } else {
                alert(" Lỗi: " + (res.message || "Gửi thất bại"));
                return false;
            }
        } catch (error) {
            console.error("Lỗi khi gửi thông báo:", error);
            alert(" Lỗi kết nối máy chủ!");
            return false;
        } finally {
            setIsSending(false);
        }
    };

    return {
        isSending,
        handleSendNotification
    };
};

export default useBroadcastCenter;