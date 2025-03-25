// server.js (Node.js backend)
const express = require('express');
const nodemailer = require('nodemailer');
const admin = require('firebase-admin');
const app = express();

app.use(express.json());

// Initialize Firebase Admin
admin.initializeApp({
    credential: admin.credential.applicationDefault()
});
const db = admin.firestore();

// Configure Nodemailer
const transporter = nodemailer.createTransport({
    service: 'gmail', // or your email service
    auth: {
        user: 'chhetrikushal14@gmail.com',
        pass: 'kushal17511779' // Use App Password if 2FA is enabled
    }
});

app.post('/send-verification-code', async (req, res) => {
    const { email, verificationCode } = req.body;

    const mailOptions = {
        from: 'chhetrikushal14@gmail.com',
        to: email,
        subject: 'Password Reset Verification Code',
        text: `Your verification code is: ${verificationCode}`,
        html: `<p>Your verification code is: <strong>${verificationCode}</strong></p>`
    };

    try {
        await transporter.sendMail(mailOptions);
        res.status(200).send({ message: 'Verification code sent successfully' });
    } catch (error) {
        console.error('Error sending email:', error);
        res.status(500).send({ error: 'Failed to send verification code' });
    }
});

const PORT = 3000;
app.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
});