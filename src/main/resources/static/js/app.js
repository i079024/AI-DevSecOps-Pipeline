// API Base URL
const API_BASE_URL = '/api/users';

// DOM Elements
const userForm = document.getElementById('userForm');
const usersTable = document.getElementById('usersTable');
const usersTableBody = document.getElementById('usersTableBody');
const loadingDiv = document.getElementById('loading');
const errorDiv = document.getElementById('error');

// Application State
let users = [];
let editingUserId = null;

// Initialize the application
document.addEventListener('DOMContentLoaded', function() {
    loadUsers();
    setupEventListeners();
});

// Setup event listeners
function setupEventListeners() {
    userForm.addEventListener('submit', handleFormSubmit);
}

// Load all users from the API
async function loadUsers() {
    try {
        showLoading(true);
        hideError();
        
        const response = await fetch(API_BASE_URL);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        users = await response.json();
        renderUsersTable();
        showLoading(false);
        
    } catch (error) {
        console.error('Error loading users:', error);
        showError('Failed to load users. Please try again.');
        showLoading(false);
    }
}

// Handle form submission
async function handleFormSubmit(event) {
    event.preventDefault();
    
    const formData = new FormData(userForm);
    const userData = {
        name: formData.get('name').trim(),
        email: formData.get('email').trim()
    };
    
    // Basic client-side validation
    if (!userData.name || !userData.email) {
        showError('Please fill in all required fields.');
        return;
    }
    
    if (!isValidEmail(userData.email)) {
        showError('Please enter a valid email address.');
        return;
    }
    
    try {
        if (editingUserId) {
            await updateUser(editingUserId, userData);
        } else {
            await createUser(userData);
        }
        
        userForm.reset();
        editingUserId = null;
        updateFormButton();
        loadUsers();
        
    } catch (error) {
        console.error('Error saving user:', error);
    }
}

// Create a new user
async function createUser(userData) {
    try {
        const response = await fetch(API_BASE_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(userData)
        });
        
        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || `HTTP error! status: ${response.status}`);
        }
        
        showSuccess('User created successfully!');
        
    } catch (error) {
        showError(error.message || 'Failed to create user. Please try again.');
        throw error;
    }
}

// Update an existing user
async function updateUser(userId, userData) {
    try {
        const response = await fetch(`${API_BASE_URL}/${userId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(userData)
        });
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        showSuccess('User updated successfully!');
        
    } catch (error) {
        showError('Failed to update user. Please try again.');
        throw error;
    }
}

// Delete a user
async function deleteUser(userId) {
    if (!confirm('Are you sure you want to delete this user?')) {
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE_URL}/${userId}`, {
            method: 'DELETE'
        });
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        showSuccess('User deleted successfully!');
        loadUsers();
        
    } catch (error) {
        console.error('Error deleting user:', error);
        showError('Failed to delete user. Please try again.');
    }
}

// Edit a user
function editUser(userId) {
    const user = users.find(u => u.id === userId);
    if (!user) {
        showError('User not found.');
        return;
    }
    
    document.getElementById('userName').value = user.name;
    document.getElementById('userEmail').value = user.email;
    editingUserId = userId;
    updateFormButton();
    
    // Scroll to form
    document.querySelector('.user-form-section').scrollIntoView({ 
        behavior: 'smooth' 
    });
}

// Cancel editing
function cancelEdit() {
    userForm.reset();
    editingUserId = null;
    updateFormButton();
}

// Update form button text based on editing state
function updateFormButton() {
    const submitButton = userForm.querySelector('button[type="submit"]');
    if (editingUserId) {
        submitButton.textContent = 'Update User';
        submitButton.style.backgroundColor = '#28a745';
        
        // Add cancel button if not exists
        if (!userForm.querySelector('.cancel-btn')) {
            const cancelButton = document.createElement('button');
            cancelButton.type = 'button';
            cancelButton.className = 'cancel-btn action-btn';
            cancelButton.textContent = 'Cancel';
            cancelButton.style.backgroundColor = '#6c757d';
            cancelButton.style.marginLeft = '10px';
            cancelButton.onclick = cancelEdit;
            submitButton.parentNode.appendChild(cancelButton);
        }
    } else {
        submitButton.textContent = 'Add User';
        submitButton.style.backgroundColor = '';
        
        // Remove cancel button if exists
        const cancelButton = userForm.querySelector('.cancel-btn');
        if (cancelButton) {
            cancelButton.remove();
        }
    }
}

// Render the users table
function renderUsersTable(userList = null) {
    const usersToRender = userList || users;
    
    if (usersToRender.length === 0) {
        usersTable.style.display = 'none';
        usersTableBody.innerHTML = '<tr><td colspan="5" style="text-align: center; padding: 2rem;">No users found. Add some users to get started!</td></tr>';
        return;
    }
    
    usersTable.style.display = 'table';
    
    usersTableBody.innerHTML = usersToRender.map(user => `
        <tr class="fade-in">
            <td>${user.id}</td>
            <td>${escapeHtml(user.name)}</td>
            <td>${escapeHtml(user.email)}</td>
            <td>${formatDate(user.createdAt)}</td>
            <td>
                <button class="action-btn edit-btn" onclick="editUser(${user.id})">
                    Edit
                </button>
                <button class="action-btn delete-btn" onclick="deleteUser(${user.id})">
                    Delete
                </button>
            </td>
        </tr>
    `).join('');
}

// Utility functions
function showLoading(show) {
    loadingDiv.style.display = show ? 'block' : 'none';
    usersTable.style.display = show ? 'none' : (users.length > 0 ? 'table' : 'none');
}

function showError(message) {
    errorDiv.textContent = message;
    errorDiv.style.display = 'block';
    setTimeout(hideError, 5000);
}

function hideError() {
    errorDiv.style.display = 'none';
}

function showSuccess(message) {
    // Remove existing success message
    const existingSuccess = document.querySelector('.success');
    if (existingSuccess) {
        existingSuccess.remove();
    }
    
    // Create and show success message
    const successDiv = document.createElement('div');
    successDiv.className = 'success';
    successDiv.textContent = message;
    
    const form = document.querySelector('.user-form-section');
    form.appendChild(successDiv);
    
    setTimeout(() => {
        if (successDiv.parentNode) {
            successDiv.remove();
        }
    }, 3000);
}

function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString() + ' ' + date.toLocaleTimeString();
}

// Enhanced API functions for new endpoints
async function searchUsers(query) {
    try {
        const response = await fetch(`${API_BASE_URL}/search?query=${encodeURIComponent(query)}`);
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        const users = await response.json();
        renderUsersTable(users);
    } catch (error) {
        console.error('Error searching users:', error);
        showError('Failed to search users. Please try again.');
    }
}

async function getUserCount() {
    try {
        const response = await fetch(`${API_BASE_URL}/count`);
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        const data = await response.json();
        return data.totalUsers;
    } catch (error) {
        console.error('Error getting user count:', error);
        return 0;
    }
}

async function loadUsersPaginated(page = 0, size = 10, sortBy = 'id', sortDir = 'asc') {
    try {
        const response = await fetch(`${API_BASE_URL}/paginated?page=${page}&size=${size}&sortBy=${sortBy}&sortDir=${sortDir}`);
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        const data = await response.json();
        renderUsersTable(data.users);
        updatePaginationInfo(data);
    } catch (error) {
        console.error('Error loading paginated users:', error);
        showError('Failed to load paginated users. Please try again.');
    }
}

function updatePaginationInfo(data) {
    // Update pagination display (if you want to add pagination UI)
    console.log(`Page ${data.currentPage + 1} of ${data.totalPages}, Total: ${data.totalItems}`);
}

// Add search functionality to the form
function addSearchCapability() {
    if (document.getElementById('searchInput')) return; // Already added
    
    const formSection = document.querySelector('.user-form-section');
    const searchDiv = document.createElement('div');
    searchDiv.innerHTML = `
        <div class="form-group">
            <label for="searchInput">🔍 Search Users:</label>
            <input type="text" id="searchInput" placeholder="Search by name or email...">
            <button type="button" id="searchBtn">Search</button>
            <button type="button" id="clearSearchBtn">Clear</button>
        </div>
    `;
    formSection.appendChild(searchDiv);
    
    // Add event listeners
    document.getElementById('searchBtn').onclick = () => {
        const query = document.getElementById('searchInput').value.trim();
        if (query) {
            searchUsers(query);
        }
    };
    
    document.getElementById('clearSearchBtn').onclick = () => {
        document.getElementById('searchInput').value = '';
        loadUsers(); // Reload all users
    };
    
    // Search on Enter key
    document.getElementById('searchInput').onkeypress = (e) => {
        if (e.key === 'Enter') {
            document.getElementById('searchBtn').click();
        }
    };
}

// Initialize enhanced features
document.addEventListener('DOMContentLoaded', function() {
    loadUsers();
    setupEventListeners();
    addSearchCapability();
    
    // Display user count
    getUserCount().then(count => {
        const header = document.querySelector('header p');
        if (header) {
            header.textContent = `User Management System - ${count} users`;
        }
    });
});

// Global functions for button clicks
window.editUser = editUser;
window.deleteUser = deleteUser;
window.searchUsers = searchUsers;