import React from 'react';

interface NavButtonProps {
    active: boolean;
    onClick: () => void;
    children: React.ReactNode;
}

export const NavButton: React.FC<NavButtonProps> = ({ active, onClick, children }) => (
    <button 
        onClick={onClick}
        className={`px-3 md:px-6 py-2 text-lg md:text-xl uppercase tracking-wider transition-all border-b-4 ${active ? 'border-red-600 text-red-500 bg-red-900/20' : 'border-transparent text-gray-400 hover:text-white hover:bg-white/5'}`}
    >
        {children}
    </button>
);
