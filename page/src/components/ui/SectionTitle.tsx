import React from 'react';

interface SectionTitleProps {
    children: React.ReactNode;
}

export const SectionTitle: React.FC<SectionTitleProps> = ({ children }) => (
    <h2 className="text-3xl text-red-500 mb-6 border-l-4 border-red-600 pl-3 uppercase tracking-widest text-shadow">
        {children}
    </h2>
);
